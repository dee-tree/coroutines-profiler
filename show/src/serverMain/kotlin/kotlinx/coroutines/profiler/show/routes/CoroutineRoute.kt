package kotlinx.coroutines.profiler.show.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.profiler.show.serialization.CoroutineProbeFrame

fun Route.coroutineRoute() {
    get("/coroutine{id}") {
        val id = call.parameters["id"]?.toLong() ?: call.respond(HttpStatusCode.BadRequest, "Coroutine id must be integer!")
        println("Requested coroutine #${id}")


        call.respond("All ok!")
    }
}

@kotlinx.serialization.Serializable
class CoroutineSuspensionStackFrame(
    val frame: String, // frame of stacktrace
    private var _suspensions: Int, // how times it probed in suspended
) {

    val suspensions: Int
        get() = _suspensions

    val children: List<CoroutineSuspensionStackFrame>
        get() = _children

    private var _children = mutableListOf<CoroutineSuspensionStackFrame>()


    companion object {
        fun fromProbesFrame(probesFrame: CoroutineProbeFrame) {
            val roots = mutableListOf<CoroutineSuspensionStackFrame>()
            probesFrame.walk { probeFrame ->

                probeFrame.stacktrace
            }
        }

    }

    fun walk(action: (CoroutineSuspensionStackFrame) -> Unit) {
        action(this)

        children?.forEach {
            it.walk(action)
        }

    }

    fun find(condition: (CoroutineSuspensionStackFrame) -> Boolean): CoroutineSuspensionStackFrame? {
        if (condition(this)) return this

        children.forEach { child ->
            child.find(condition)?.let { return it }
        }
        return null
    }
}