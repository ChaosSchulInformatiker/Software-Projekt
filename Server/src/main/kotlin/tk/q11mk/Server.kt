@file:JvmName("Server")

package tk.q11mk

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.serialization.Serializable
import tk.q11mk.accounts.receiveCode
import tk.q11mk.accounts.sendCode
import tk.q11mk.schedule.Schedule
import tk.q11mk.utils.getPublicProperty

fun main() {
    embeddedServer(CIO, port = getPublicProperty("port").toInt()) {
        install(ContentNegotiation) {
            json()
        }

        routing {
            get("/schedule/{id}") {
                val id = call.parameters["id"]

                if (id == "000000") {
                    call.response(exampleSchedule)
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

                call.response(sendCode(firstName, lastName))
            }

            get("/login") {
                val email = call.request.queryParameters["e_mail"]
                val code = call.request.queryParameters["code"]?.toIntOrNull()

                if (email == null || code == null) {
                    call.respond400()
                    return@get
                }

                call.response(receiveCode(email, code))
            }
        }
    }.start(wait = true)
}

suspend inline fun <reified T> ApplicationCall.response(vararg values: T) = respond(Response(200, values))
suspend inline fun ApplicationCall.respond400() = respond(Code400)

@Serializable
class Response<T>(
    @Suppress("unused") val status: Int,
    @Suppress("unused") val result: Array<T>
) {
    @Suppress("unused") val timestamp: Long = System.currentTimeMillis()
}

val Code400 get() = Response<String>(400, emptyArray())

val exampleSchedule = Schedule(
    Schedule.Day(listOf(null, null,
        Schedule.Lesson("M", "SCHN", "7A"),
        Schedule.Lesson("M", "SCHN", "7A"),
    )),
    Schedule.Day(listOf()),
    Schedule.Day(listOf()),
    Schedule.Day(listOf()),
    Schedule.Day(listOf()),
)