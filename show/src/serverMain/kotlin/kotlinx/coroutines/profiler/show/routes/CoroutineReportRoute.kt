package kotlinx.coroutines.profiler.show.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.initializeCoroutinesIfNot

fun Route.coroutineReportRoute() {
    get("/coroutineReport{id}") {
        val id = call.parameters["id"]?.toLong() ?: call.respond(HttpStatusCode.BadRequest, "Coroutine id must be integer!")
        println("Requested coroutine #${id}")

        initializeCoroutinesIfNot()

        call.respond(ProfilingStorage.linearCoroutinesStructure.coroutines.find { it.id == id }!!)
    }
}

fun Route.allCoroutinesIdsRoute() {
    get("/coroutinesIds") {
        initializeCoroutinesIfNot()

        call.respond(ProfilingStorage.linearCoroutinesStructure.coroutines.map { it.id })
    }
}