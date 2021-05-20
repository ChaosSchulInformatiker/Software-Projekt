package tk.q11mk

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.pipeline.*
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import tk.q11mk.schedule.Schedule

fun main() {
    embeddedServer(Netty, port = 80) {
        routing {
            get("/schedule/{id}") {
                val id = call.parameters["id"]

                if (id == "000000") {
                    call.respondJsonSerializable(exampleSchedule)
                } else {
                    call.respond400()
                }

            }

            post("/register/{id}") {
                call.respondText("Ok")
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