package kotlinx.coroutines.profiler.visual

import kotlinx.coroutines.debug.State
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.visual.CoroutineStatesRange.Companion.splitByStates
import kotlinx.serialization.json.*
import java.io.OutputStream


@Suppress("EXPERIMENTAL_API_USAGE")
fun List<ProfilingCoroutineInfo>.toFlameJson(out: OutputStream) {
    val json = buildJsonObject {
        put("name", "root")
        val samplesTotally = fold(0L) { acc, current -> acc + current.samples.size }
        put("value", samplesTotally)

        putJsonArray("children") {
            this@toFlameJson.forEach { rootCoroutine ->
                rootCoroutine.toFlameJson(
                    this,
                    1.0,
                    rootCoroutine.samples.first().dumpId..rootCoroutine.samples.last().dumpId
                )
            }
        }
    }

    out.bufferedWriter().use {
        it.write(json.toString())
    }
}


@Suppress("EXPERIMENTAL_API_USAGE")
private fun ProfilingCoroutineInfo.toFlameJson(
    array: JsonArrayBuilder,
    parentCoeff: Double,
    parentDumpsIds: LongRange
) {
    val states = this@toFlameJson.splitByStates()

    states.forEach { state ->

        val samplesForThisParentState = state.samplesRange.intersect(parentDumpsIds).size
        val width = (samplesForThisParentState * parentCoeff).toLong()
        if (width > 0) {
            array.addJsonObject {
                put("name", "$kind #$id")
                put("state", state.state.toString())
                put("samples", samplesForThisParentState)
                put("stacktrace", state.lastStackTrace.joinToString(", "))
                put("creationStackTrace", this@toFlameJson.creationStackTrace.joinToString(", "))
                put("thread", state.thread)
                put("value", width)
                putJsonArray("children") {
                    children.forEach { child ->
                        child.toFlameJson(this, 1.0 / children.size, state.samplesRange)
                    }
                }
            }
        }
    }
}

