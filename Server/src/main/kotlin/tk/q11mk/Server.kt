@file:JvmName("Server")

package tk.q11mk

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import tk.q11mk.accounts.getEmailAccount
import tk.q11mk.accounts.receiveCode
import tk.q11mk.accounts.sendCode
import tk.q11mk.database.Database
import tk.q11mk.schedule.Schedule
import tk.q11mk.utils.getPublicProperty
import tk.q11mk.utils.getSecretProperty

fun main() {
    embeddedServer(CIO, port = getPublicProperty("port").toInt()) {
        routing {
            get("/schedule/{id}") {
                val id = call.parameters["id"]

                if (id == "000000") {
                    call.respondJsonSerializable(exampleSchedule)
                } else {
                    call.respond400()
                }
            }

            get("/register") {
                val firstName = call.request.queryParameters["first_name"]
                val lastName = call.request.queryParameters["last_name"]

                if (firstName == null || lastName == null) {
                    call.respond400()
                    return@get
                }

                call.respondJsonSerializable(sendCode(firstName, lastName))
            }

            get("/login") {
                val email = call.request.queryParameters["e_mail"]
                val code = call.request.queryParameters["code"]?.toIntOrNull()

                if (email == null || code == null) {
                    call.respond400()
                    return@get
                }

                call.respondJsonSerializable(receiveCode(email, code))
            }
        }
    }.start(wait = true)
}

suspend fun ApplicationCall.respondJsonSerializable(json: JSONSerializable) {
    var jsonObject: JSONObject? = null
    val responseCode = try {
        jsonObject = json.serialize()
        200
    } catch (e: RequestException) {
        e.responseCode
    } catch (e: Throwable) {
        500
    }
    respondJsonObject(jsonObject ?: JSONObject(), responseCode)
}

suspend fun ApplicationCall.respondJsonObject(json: JSONObject, responseCode: Int = 200) {
    respondJson(JSONArray().apply { add(json) }, responseCode)
}

suspend fun ApplicationCall.respond400() {
    respondJson(JSONArray(), 400)
}

suspend fun ApplicationCall.respondJson(json: JSONArray, responseCode: Int = 200) {
    val root = JSONObject()

    //root["responseCode"] = responseCode

    root["timestamp"] = System.currentTimeMillis()

    root["result"] = json

    respondText(root.toJSONString(), status = HttpStatusCode.fromValue(responseCode))
}

private val exampleSchedule = Schedule(
    Schedule.Day(listOf(null, null,
        Schedule.Lesson("M", "SCHN", "7A"),
        Schedule.Lesson("M", "SCHN", "7A"),
    )),
    Schedule.Day(listOf()),
    Schedule.Day(listOf()),
    Schedule.Day(listOf()),
    Schedule.Day(listOf()),
)