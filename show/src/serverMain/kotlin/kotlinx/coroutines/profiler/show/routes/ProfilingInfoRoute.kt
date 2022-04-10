@file:Suppress("EXPERIMENTAL_API_USAGE")

package kotlinx.coroutines.profiler.show.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.profiler.show.extractInfo
import kotlinx.coroutines.profiler.show.loadProfilingResults
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.profilingInfo
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.profilingResults
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.profilingResultsFile

fun Route.profilingInfoRoute() {
    get("/profilingInfo") {

        if (!profilingResultsFile.exists()) {
            this.call.respond(HttpStatusCode.NotFound, "Profiling results not found!")
            return@get
        }

        loadProfilingResults(profilingResultsFile)

        profilingInfo = profilingResults.extractInfo()
        call.respond(profilingInfo)
    }
}