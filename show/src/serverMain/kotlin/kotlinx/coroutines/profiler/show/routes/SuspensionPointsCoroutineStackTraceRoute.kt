package kotlinx.coroutines.profiler.show.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.profiler.show.serialization.CoroutineSuspensionsFrame.Companion.toCoroutineSuspensionsFrame
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.initializeCoroutinesIfNot

fun Route.suspensionPointsCoroutineStackTraceRoute() {
    get("/suspensionsStackTrace/{id}") {

        val id = call.parameters["id"]?.toLong() ?: run {
            call.respond(HttpStatusCode.BadRequest, "Coroutine id is invalid!")
        }
        println("Requested suspensions stack trace for coroutine #${id}")

        initializeCoroutinesIfNot()

        val selectedCoroutine = ProfilingStorage.linearCoroutinesStructure.coroutines.find { it.id == id }!!

        call.respond(selectedCoroutine.toCoroutineSuspensionsFrame())

    }

    get("/suspensionsStackTrace") {
        println("Requested suspensions stack trace!")

        initializeCoroutinesIfNot()

        call.respond(ProfilingStorage.linearCoroutinesStructure.toCoroutineSuspensionsFrame())
    }

}