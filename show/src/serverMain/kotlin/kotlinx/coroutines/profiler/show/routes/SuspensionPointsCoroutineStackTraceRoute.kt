package kotlinx.coroutines.profiler.show.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.profiler.show.serialization.CoroutineSuspensionsFrame.Companion.toCoroutineSuspensionsFrame
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage

fun Route.suspensionPointsCoroutineStackTraceRoute() {
    get("/suspensionsStackTrace{id}") {
        println("Requested suspensions stack trace!")

        val id = call.parameters["id"]?.toLong() ?: run {
            call.respond(HttpStatusCode.BadRequest, "Coroutine id is invalid!")
        }
        println("coroutine #${id}")

        if (!ProfilingStorage.isCoroutinesProbesInitialized()) {
            call.respond(HttpStatusCode.BadRequest, "Profiling probes are not initialized!")
            return@get
        }
        if (!ProfilingStorage.isLinearCoroutinesStructureInitialized()) {
            call.respond(HttpStatusCode.BadRequest, "Coroutines structure is not initialized!")
            return@get
        }

        val selectedCoroutine = ProfilingStorage.linearCoroutinesStructure.coroutines.find { it.id == id }!!

        call.respond(selectedCoroutine.toCoroutineSuspensionsFrame())

    }
}