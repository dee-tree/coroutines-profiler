@file:Suppress("EXPERIMENTAL_API_USAGE")

package kotlinx.coroutines.profiler.show.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo.Companion.bindWithInfos
import kotlinx.coroutines.profiler.show.loadProfilingResults
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.coroutinesProbes
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.coroutinesStructure
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.profilingResults
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.profilingResultsFile
import kotlinx.coroutines.profiler.show.toProbeFrame


fun Route.stacksRoute() {
    get("/stacks") {

        if (!profilingResultsFile.exists()) {
            this.call.respond(HttpStatusCode.NotFound, "Profiling results not found!")
            return@get
        }

        if (!ProfilingStorage.isProfilingResultsInitialized()) {
            loadProfilingResults(profilingResultsFile)
        }

        coroutinesStructure = profilingResults.loadStructureFromFile()
        coroutinesProbes = profilingResults.loadProbesFromFile()
        val coroutines = coroutinesProbes.bindWithInfos(coroutinesStructure)
        val rootStackFrame = coroutines.toProbeFrame()

        call.respond(rootStackFrame)
    }
}