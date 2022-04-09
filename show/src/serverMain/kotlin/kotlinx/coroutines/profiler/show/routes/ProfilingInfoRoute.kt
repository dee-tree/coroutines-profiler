@file:Suppress("EXPERIMENTAL_API_USAGE")

package kotlinx.coroutines.profiler.show.routes

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.profiler.sampling.statistics.ProfilingStatistics
import kotlinx.coroutines.profiler.show.extractInfo
import kotlinx.coroutines.profiler.show.storage.profilingInfo
import kotlinx.coroutines.profiler.show.storage.profilingResults
import kotlinx.coroutines.profiler.show.storage.profilingResultsFile

fun Route.profilingInfoRoute() {
    get("/profilingInfo") {
        profilingResults = ProfilingStatistics.fromFile(profilingResultsFile)
        profilingInfo = profilingResults.extractInfo()
        call.respond(profilingInfo)
    }
}