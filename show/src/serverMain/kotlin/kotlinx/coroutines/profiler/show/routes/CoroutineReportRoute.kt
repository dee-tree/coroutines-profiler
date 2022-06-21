package kotlinx.coroutines.profiler.show.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.profiler.core.data.State
import kotlinx.coroutines.profiler.show.CoroutineStateRange.Companion.splitByStates
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage.initializeCoroutinesIfNot

fun Route.coroutineReportRoute() {
    get("/coroutineReport{id}") {
        val id =
            call.parameters["id"]?.toLong() ?: call.respond(HttpStatusCode.BadRequest, "Coroutine id must be long!")
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

fun Route.coroutineRangesCountAtState() {
    get("/coroutineSwitchedTimesAtState{id}{state}") {
        val id =
            call.parameters["id"]?.toLong() ?: call.respond(HttpStatusCode.BadRequest, "Coroutine id must be long!")
        val state = call.parameters["state"]?.let { State.valueOf(it) } ?: call.respond(
            HttpStatusCode.BadRequest,
            "Coroutine id must be integer!"
        )
        println("Requested coroutine #${id} state ${state} switches times")

        initializeCoroutinesIfNot()

        val rangesCount = ProfilingStorage.linearCoroutinesStructure
            .coroutines
            .find { it.id == id }
            ?.probes?.splitByStates(true, true)
            ?.count { it.state == state } ?: 0

        call.respond(rangesCount)
    }
}

fun Route.coroutineSuspensionsCountWithSameStacktracesLikeProbeFrame() {
    get("/coroutineSuspensionsCountWithStacktraceInProbe{id}{probeId}") {
        val id =
            call.parameters["id"]?.toLong() ?: call.respond(HttpStatusCode.BadRequest, "Coroutine id must be long!")
        val probeId = call.parameters["probeId"]?.toInt() ?: call.respond(
            HttpStatusCode.BadRequest,
            "Coroutine id must be integer!"
        )

        initializeCoroutinesIfNot()

        val exampleProbe = ProfilingStorage.coroutinesProbes.probes.find { it.probeId == probeId }!!
        val rangesCount = ProfilingStorage.linearCoroutinesStructure
            .coroutines
            .find { it.id == id }
            ?.probes?.splitByStates(true, true)
            ?.count { it.state == State.SUSPENDED && it.lastStackTrace == exampleProbe.lastUpdatedStackTrace } ?: 0

        call.respond(rangesCount)
    }
}

fun Route.coroutineProbesCountAtState() {
    get("/coroutineProbesCount{id}{state}") {
        val id =
            call.parameters["id"]?.toLong() ?: call.respond(HttpStatusCode.BadRequest, "Coroutine id must be long!")
        val state = call.parameters["state"]?.let { State.valueOf(it) } ?: call.respond(
            HttpStatusCode.BadRequest,
            "Coroutine id must be integer!"
        )
        println("Requested coroutine #${id} probes at state ${state}")

        initializeCoroutinesIfNot()

        val probesCount = ProfilingStorage.linearCoroutinesStructure
            .coroutines
            .find { it.id == id }
            ?.probes?.splitByStates(true, true)
            ?.filter { it.state == state }
            ?.sumOf { it.probesRange.last - it.probesRange.first + 1 } ?: 0

        call.respond(probesCount)
    }
}