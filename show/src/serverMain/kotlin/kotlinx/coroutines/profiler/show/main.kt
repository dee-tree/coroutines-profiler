@file:Suppress("EXPERIMENTAL_API_USAGE")

package kotlinx.coroutines.profiler.show

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.profiler.show.routes.*
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage
import java.io.File


fun main(args: Array<String>) {
    // arg is path to profiling result file
    ProfilingStorage.setProfilingResultsFile(File(args[0]))

    embeddedServer(Netty, 9090, watchPaths = listOf("classes", "resources")) {
        install(ContentNegotiation) {
            json()
        }

        install(StatusPages) {
            exception<Throwable> { error ->
                System.err.println("Exception occurred!")
                System.err.println(error.message)
                error.printStackTrace(System.err)
                System.err.println(error)
            }
        }

        routing {
            staticRoute()
            profilingStatisticsRoute()
            stacksRoute()
            allCoroutinesIdsRoute()
            coroutineReportRoute()
            coroutineRangesCountAtState()
            coroutineProbesCountAtState()
            suspensionsCountAtSameStatesLikeProbeRangeId()
            suspensionPointsCoroutineStackTraceRoute()
            threadsRoute()
        }
    }.start(wait = true)
}
