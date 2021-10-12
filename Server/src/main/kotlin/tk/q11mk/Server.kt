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
import tk.q11mk.accounts.changeClassData
import tk.q11mk.accounts.receiveCode
import tk.q11mk.accounts.sendCode
import tk.q11mk.database.classesTable
import tk.q11mk.database.getAccountFromId
import tk.q11mk.schedule.Schedule
import tk.q11mk.utils.getPublicProperty

fun main() {
    embeddedServer(CIO, port = getPublicProperty("port").toInt()) {
        install(ContentNegotiation) {
            json()
        }

        routing {
            get("/schedule") {
                val dayIndex = call.request.queryParameters["day"]?.toIntOrNull()
                val clazz = call.request.queryParameters["class"]
                val subjectsCSV = call.request.queryParameters["subjects"]
                val authorization = call.request.headers["Authorization"]?.toLongOrNull()

                if (dayIndex == null || clazz == null || subjectsCSV == null) {
                    call.respond400()
                    return@get
                }
                if (authorization == null || getAccountFromId(authorization) == null) {
                    call.respond403()
                    return@get
                }

                call.response(Schedule.Day.fromRequest(dayIndex, clazz, subjectsCSV))
            }

            get("/classes") {
                //'{inf=Informatik, b=Biologie, c=Chemie, sw=Sport weiblich, d=Deutsch, e=Englisch, g=Geschichte, mu=Musik, ku=Kunsterziehung, L=Latein, m=Mathematik, geo=Erdkunde, ev=Evang.Rel., cue=null, ph=Physik, eth=Ethik, sk=Sozialkunde, rk=Kath.Rel., sm=Sport männlich, wr=Wirtschaft, phue=null}'
                val authorization = call.request.headers["Authorization"]?.toLongOrNull()

                if (authorization == null || getAccountFromId(authorization) == null) {
                    call.respond403()
                    return@get
                }

                call.response(*(classesTable.getColumns("nameId", "subjects").getOrThrow().let { l ->
                    Array(l.size) { i ->
                        ClassResponse(l[i][0].toString(), l[i][1].toString().let { s ->
                            s.substring(1, s.length - 1).split(", ").takeIf { it.size > 2 }
                                ?.associate { it.split("=").let { t -> if(t.size != 2) println(s); t[0] to t[1] } } ?: emptyMap()
                        })
                    }
                }))
            }

            get("/register") {
                val firstName = call.request.queryParameters["first_name"]
                val lastName = call.request.queryParameters["last_name"]
                val email = call.request.queryParameters["e_mail"]

                if (firstName == null || lastName == null || email == null) {
                    call.respond400()
                    return@get
                }

                if (!email.endsWith("@maristenkolleg.de")) {
                    call.respond400()
                    return@get
                }

                call.response(sendCode(firstName, lastName, email))
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

            get("/change_class_data") {
                val id = call.request.queryParameters["id"]
                val clazz = call.request.queryParameters["class"]
                val subjects = call.request.queryParameters["subjects"]

                if (id == null || clazz == null || subjects == null) {
                    call.respond400()
                    return@get
                }

                call.response(changeClassData(id, clazz, subjects))
            }
        }
    }.start(wait = true)
}

suspend inline fun <reified T> ApplicationCall.response(vararg values: T) {
    response.header("Access-Control-Allow-Origin", "*")
    respond(Response(200, values))
}
suspend inline fun ApplicationCall.respond400() = respond(Code400)
suspend inline fun ApplicationCall.respond403() = respond(Code403)

@Serializable
class Response<T>(
    @Suppress("unused") val status: Int,
    @Suppress("unused") val result: Array<T>
) {
    @Suppress("unused") val timestamp: Long = System.currentTimeMillis()
}

val Code400 get() = Response<String>(400, emptyArray())
val Code403 get() = Response<String>(403, emptyArray())

@Serializable
class ClassResponse(
    val name: String,
    val subjects: Map<String, String>
)

/*val exampleSchedule = Schedule(
    Schedule.Day(listOf(
        null, 
        null,
        Schedule.Lesson("M", "KONR", "K2"),
        Schedule.Lesson("M", "KONR", "K2"),
        Schedule.Lesson("D", "STEU", "K4"),
        Schedule.Lesson("K", "DIEP", "K3"),
        null,
        Schedule.Lesson("Ph", "SPEN", "Ph1"),
        Schedule.Lesson("E", "FRIS", "K3"),
        Schedule.Lesson("DW", "AUDE", "5A"),
        Schedule.Lesson("DW", "AUDE", "5A"),
    )),
    Schedule.Day(listOf(
        null,
        null,
        Schedule.Lesson("Inf", "POHL", "EDV5"),
        Schedule.Lesson("Inf", "POHL", "EDV5"),
        null,
        null,
        null,
        Schedule.Lesson("Sp", "WEGS", "TA"),
        Schedule.Lesson("Sp", "WEGS", "TA"),
        Schedule.Lesson("GeoW", "WEGS", "K6"),
        Schedule.Lesson("GeoW", "WEGS", "K6"),
    )),
    Schedule.Day(listOf(
        Schedule.Lesson("D", "STEU", "K4"),
        Schedule.Lesson("D", "STEU", "K4"),
        Schedule.Lesson("E", "FRIS", "K3"),
        Schedule.Lesson("E", "FRIS", "K3"),
        Schedule.Lesson("M", "KONR", "K2"),
        Schedule.Lesson("Phi", "STEH", "K5"),
        null,
        null,
        null,
        Schedule.Lesson("Mu", "HESS", "Mu2"),
        Schedule.Lesson("Mu", "HESS", "Mu2"),
    )),
    Schedule.Day(listOf(
        null,
        Schedule.Lesson("K", "DIEP", "K3"),
        Schedule.Lesson("WR", "MAIR", "K3"),
        Schedule.Lesson("D", "STEU", "K4"),
        Schedule.Lesson("Inf", "POHL", "EDV5"),
        null,
        null,
        null,
        Schedule.Lesson("G", "SCHJ", "K2"),
        Schedule.Lesson("M", "KONR", "K2"),
        Schedule.Lesson("Ph", "SPEN", "Ph1"),
    )),
    Schedule.Day(listOf(
        Schedule.Lesson("Ph", "SPEN", "Ph1"),
        Schedule.Lesson("E", "FRIS", "K3"),
        Schedule.Lesson("G", "SCHJ", "K2"),
        Schedule.Lesson("Sk", "SCHJ", "K2"),
        Schedule.Lesson("WR", "MAIR", "K3"),
        Schedule.Lesson("Phi", "STEH", "K5"),
    )),
)*/
