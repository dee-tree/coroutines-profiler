package kotlinx.coroutines.profiler.show

import kotlinx.coroutines.debug.State
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.show.CoroutineStatesRange.Companion.splitByStates
import kotlinx.serialization.json.*
import java.io.OutputStream


@Suppress("EXPERIMENTAL_API_USAGE")
fun List<ProfilingCoroutineInfo>.toFlameJson(out: OutputStream) {
    val json = buildJsonObject {
        put("name", "root")
        val samplesTotally = maxOf { it.samples.last().dumpId }
        put("value", samplesTotally)

        putJsonArray("children") {
            this@toFlameJson.forEach { rootCoroutine ->
                rootCoroutine.toFlameJson(
                    this,
                    1.0 / size,
                    0..samplesTotally
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
                put("name", "$name $id")
                put("id", id)
                put("state", state.state.toString())
                put("samples", samplesForThisParentState)
                put("stacktrace", state.lastStackTrace.joinToString(", "))
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


internal class CoroutineStatesRange private constructor(
    val state: State,
    val thread: String?,
    val lastStackTrace: List<String>,
    fromSample: Long,
    toSample: Long
) {
    private var _fromSample = fromSample
    private var _toSample = toSample

    val fromSample get() = _fromSample
    val toSample get() = _toSample

    val samplesRange get() = fromSample..toSample

    companion object {
        internal fun ProfilingCoroutineInfo.splitByStates(): List<CoroutineStatesRange> {
            val split = mutableListOf<CoroutineStatesRange>()

            samples.forEach {
                val last = split.lastOrNull()
                if (it.state == last?.state && it.currentThreadName == last.thread && it.currentStackTrace == last.lastStackTrace) {
                    split[split.lastIndex]._toSample = it.dumpId
                } else {
                    split.add(
                        CoroutineStatesRange(
                            it.state,
                            it.currentThreadName,
                            it.currentStackTrace,
                            it.dumpId,
                            it.dumpId
                        )
                    )
                }
            }

            return split
        }
    }

}
