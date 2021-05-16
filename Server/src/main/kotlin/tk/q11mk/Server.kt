package tk.q11mk

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 80) {
        routing {
            get ("/") {
                call.respondText("Hallo, Welt!")
            }
        }
    }.start(wait = true)
}