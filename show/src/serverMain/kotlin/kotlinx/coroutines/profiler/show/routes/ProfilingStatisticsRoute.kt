@file:Suppress("EXPERIMENTAL_API_USAGE")

package kotlinx.coroutines.profiler.show.routes

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.initializeProfilingResultsIfNot
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.profilingResults

fun Route.profilingStatisticsRoute() {
    get("/profilingStatistics") {

        println("Requested profiling statistics")
        initializeProfilingResultsIfNot()

        call.respond(profilingResults.profilingStatistics)
    }
}