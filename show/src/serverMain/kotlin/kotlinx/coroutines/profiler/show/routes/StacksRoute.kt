@file:Suppress("EXPERIMENTAL_API_USAGE")

package kotlinx.coroutines.profiler.show.routes

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo.Companion.bindWithInfos
import kotlinx.coroutines.profiler.show.storage.coroutinesProbes
import kotlinx.coroutines.profiler.show.storage.coroutinesStructure
import kotlinx.coroutines.profiler.show.storage.profilingResults
import kotlinx.coroutines.profiler.show.toProbeFrame


fun Route.stacksRoute() {
    get("/stacks") {
        coroutinesStructure = profilingResults.loadStructureFromFile()
        coroutinesProbes = profilingResults.loadProbesFromFile()
        val coroutines = coroutinesProbes.bindWithInfos(coroutinesStructure)
        val rootStackFrame = coroutines.toProbeFrame()

        call.respond(rootStackFrame)
    }
}