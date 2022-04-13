@file:Suppress("EXPERIMENTAL_API_USAGE")

package kotlinx.coroutines.profiler.show.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.profiler.core.data.ProfilingCoroutineInfo.Companion.addProbes
import kotlinx.coroutines.profiler.core.data.StructuredProfilingCoroutineInfo.Companion.addProbes
import kotlinx.coroutines.profiler.core.data.StructuredProfilingCoroutineInfo.Companion.toStructured
import kotlinx.coroutines.profiler.core.data.loadProbes
import kotlinx.coroutines.profiler.core.data.loadStructure
import kotlinx.coroutines.profiler.core.data.readProfilingResultsFile
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.coroutinesProbes
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.linearCoroutinesStructure
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.profilingResults
import kotlinx.coroutines.profiler.show.toProbeFrame


fun Route.stacksRoute() {
    get("/stacks") {
        if (!ProfilingStorage.isProfilingResultsFileInitialized()) {
            call.respond(HttpStatusCode.BadRequest, "Profiling file not loaded!")
            return@get
        }

        if (!ProfilingStorage.profilingResultFile.exists()) {
            this.call.respond(HttpStatusCode.NotFound, "Profiling results not found!")
            return@get
        }

        if (!ProfilingStorage.isProfilingResultsInitialized()) {
            ProfilingStorage.setProfilingResults(readProfilingResultsFile(ProfilingStorage.profilingResultFile))
        }

        ProfilingStorage.setCoroutinesProbes(profilingResults.loadProbes())
        ProfilingStorage.setLinearCoroutinesStructure(
            profilingResults.loadStructure()
        )

        linearCoroutinesStructure.addProbes(coroutinesProbes.probes)

        val structuredCoroutines = linearCoroutinesStructure.toStructured()//.addProbes(coroutinesProbes.probes)

        val rootStackFrame = structuredCoroutines.toProbeFrame()

        call.respond(rootStackFrame)
    }
}