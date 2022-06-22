@file:Suppress("EXPERIMENTAL_API_USAGE")

package kotlinx.coroutines.profiler.show.routes

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.profiler.core.data.StructuredProfilingCoroutineInfo.Companion.toStructured
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.initializeCoroutinesIfNot
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.linearCoroutinesStructure
import kotlinx.coroutines.profiler.show.toProbeFrame


fun Route.stacksRoute() {
    get("/stacks") {
        ProfilingStorage.setProfilingResults(null)
        ProfilingStorage.setCoroutinesProbesRanges(null)
        ProfilingStorage.setLinearCoroutinesStructure(null)
        initializeCoroutinesIfNot()
        val structuredCoroutines = linearCoroutinesStructure.toStructured()

        val rootStackFrame = structuredCoroutines.toProbeFrame()

        call.respond(rootStackFrame)
    }
}