@file:Suppress("EXPERIMENTAL_API_USAGE")

package kotlinx.coroutines.profiler.show.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.profiler.core.data.readProfilingResultsFile
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.isProfilingResultsFileInitialized
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.profilingResultFile
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.profilingResults

fun Route.profilingStatisticsRoute() {
    get("/profilingStatistics") {

        println("Requested profiling statistics")
        if (!isProfilingResultsFileInitialized()) {
            call.respond(HttpStatusCode.BadRequest, "Profiling file not loaded!")
            return@get
        }

        if (!profilingResultFile.exists()) {
            this.call.respond(HttpStatusCode.NotFound, "Profiling results not found!")
            return@get
        }

        ProfilingStorage.setProfilingResults(readProfilingResultsFile(profilingResultFile))
        call.respond(profilingResults.profilingStatistics)
    }
}