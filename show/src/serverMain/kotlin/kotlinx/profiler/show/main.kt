package kotlinx.profiler.show

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File

fun main() {
    embeddedServer(Netty, 9090) {
        routing {
            get("/") {
                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }

            static("/") {
                resources("")
            }


            get("/hello") {
                call.respondText("Hello, API!")
            }

            get("/profilingResults") {
                call.respondFile(File("W:\\Kotlin\\Projects\\coroutines-profiler\\sample-app\\out\\results\\profile\\profiling_results.json"))
            }
        }
    }.start(wait = true)
}