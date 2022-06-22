package kotlinx.coroutines.profiler.show.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.initializeCoroutinesIfNot
import kotlinx.coroutines.profiler.show.toThreadsFrame

fun Route.threadsRoute() {
    get("/threads/{id}") {
        val id = call.parameters["id"]?.toLong() ?: run {
            call.respond(HttpStatusCode.BadRequest, "Coroutine id is invalid!")
        }

        initializeCoroutinesIfNot()

        val selectedCoroutine = ProfilingStorage.linearCoroutinesStructure.coroutines.find { it.id == id }!!

        call.respond(selectedCoroutine.toThreadsFrame())
    }
}