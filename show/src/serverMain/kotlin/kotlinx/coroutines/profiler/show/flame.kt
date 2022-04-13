package kotlinx.coroutines.profiler.show

import kotlinx.coroutines.profiler.core.data.CoroutinesStructure
import kotlinx.coroutines.profiler.core.data.State
import kotlinx.coroutines.profiler.core.data.StructuredProfilingCoroutineInfo
import kotlinx.coroutines.profiler.show.CoroutineStatesRange.Companion.splitByStates
import kotlinx.coroutines.profiler.show.serialization.CoroutineProbeFrame
import kotlinx.coroutines.profiler.show.serialization.buildCoroutineProbeFrame

@Suppress("EXPERIMENTAL_API_USAGE")
fun CoroutinesStructure.toProbeFrame(): CoroutineProbeFrame {
    val root = buildCoroutineProbeFrame {
        name = "root"

        val probesTotally = structure.maxOf { it.probes.last().probeId }.toInt()

        value = probesTotally

        structure.forEach { rootCoroutine ->
            addChildren {
                val list = rootCoroutine.toProbeFrames(1.0 / structure.size, 0..probesTotally)
                list
            }
        }
    }
    return root
}

@Suppress("EXPERIMENTAL_API_USAGE")
private fun StructuredProfilingCoroutineInfo.toProbeFrames(
    parentCompensationCoefficient: Double,
    parentDumpsIds: IntRange
): List<CoroutineProbeFrame> {
    val states = this@toProbeFrames.splitByStates()

    return buildList {
        states.forEach { state ->
            val probesForThisParentState = state.probesRange.intersect(parentDumpsIds).size
            val width = (probesForThisParentState * parentCompensationCoefficient).toInt()
            if (width > 0) {

                val child = buildCoroutineProbeFrame {
                    name = "${this@toProbeFrames.name} ${this@toProbeFrames.id}"
                    coroutineId = this@toProbeFrames.id
                    coroutineState = state.state.toString()
                    probes = probesForThisParentState
                    stacktrace = state.lastStackTrace
                    thread = state.thread
                    value = width

                    this@toProbeFrames.children.forEach {
                        addChildren {
                            it.toProbeFrames(1.0 / children.size, state.probesRange)
                        }
                    }
                }

                add(child)
            }
        }
    }
}


@Suppress("EXPERIMENTAL_API_USAGE")
internal class CoroutineStatesRange private constructor(
    val state: State,
    val thread: String?,
    val lastStackTrace: List<String>,
    fromProbeId: Int,
    toProbeId: Int
) {
    private var _fromProbeId = fromProbeId
    private var _toProbeId = toProbeId

    val fromProbeId get() = _fromProbeId
    val toProbeId get() = _toProbeId

    val probesRange get() = fromProbeId..toProbeId

    companion object {
        internal fun StructuredProfilingCoroutineInfo.splitByStates(): List<CoroutineStatesRange> {
            val split = mutableListOf<CoroutineStatesRange>()

            probes.forEach {
                val last = split.lastOrNull()
                if (it.state == last?.state && it.lastUpdatedThreadName == last.thread && it.lastUpdatedStackTrace == last.lastStackTrace) {
                    split[split.lastIndex]._toProbeId = it.probeId
                } else {
                    split.add(
                        CoroutineStatesRange(
                            it.state,
                            it.lastUpdatedThreadName,
                            it.lastUpdatedStackTrace,
                            it.probeId,
                            it.probeId
                        )
                    )
                }
            }

            return split
        }
    }

}

