@file:Suppress("EXPERIMENTAL_API_USAGE")

package kotlinx.coroutines.profiler.show

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.profiler.show.routes.profilingInfoRoute
import kotlinx.coroutines.profiler.show.routes.stacksRoute
import kotlinx.coroutines.profiler.show.routes.staticRoute
import kotlinx.coroutines.profiler.show.storage.profilingResultsFile
import java.io.File


fun main(args: Array<String>) {
    // arg is path to profiling result file
    profilingResultsFile = File(args[0])

    embeddedServer(Netty, 9090, watchPaths = listOf("classes", "resources")) {
        install(ContentNegotiation) {
            json()
        }

        routing {
            staticRoute()
            profilingInfoRoute()
            stacksRoute()
        }
    }.start(wait = true)
}
