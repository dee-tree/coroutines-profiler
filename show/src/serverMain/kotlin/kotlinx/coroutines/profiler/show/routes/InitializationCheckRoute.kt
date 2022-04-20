package kotlinx.coroutines.profiler.show.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage

suspend fun PipelineContext<Unit, ApplicationCall>.checkProfilingResultsFileInitialized() {
    if (!ProfilingStorage.isProfilingResultsFileInitialized()) {
        call.respond(HttpStatusCode.BadRequest, "Profiling file not loaded!")
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.checkProfilingResultsFileExists() {
    checkProfilingResultsFileInitialized()
    if (!ProfilingStorage.profilingResultFile.exists()) {
        this.call.respond(HttpStatusCode.NotFound, "Profiling results not found!")
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.checkCoroutinesProbesInitialized() {
    checkProfilingResultsFileExists()
    if (!ProfilingStorage.isCoroutinesProbesInitialized()) {
        call.respond(HttpStatusCode.BadRequest, "Profiling probes are not initialized!")
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.checkCoroutinesStructureInitialized() {
    checkProfilingResultsFileExists()
    if (!ProfilingStorage.isLinearCoroutinesStructureInitialized()) {
        call.respond(HttpStatusCode.BadRequest, "Coroutines structure is not initialized!")
    }
}
