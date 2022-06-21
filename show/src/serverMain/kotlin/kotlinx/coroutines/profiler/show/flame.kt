package kotlinx.coroutines.profiler.show

import kotlinx.coroutines.profiler.core.data.*
import kotlinx.coroutines.profiler.show.CoroutineStateRange.Companion.splitByStates
import kotlinx.coroutines.profiler.show.serialization.CoroutineProbeFrame
import kotlinx.coroutines.profiler.show.serialization.CoroutineThreadsFrame
import kotlinx.coroutines.profiler.show.serialization.buildCoroutineProbeFrame
import kotlinx.coroutines.profiler.show.serialization.buildCoroutineThreadsFrame

@Suppress("EXPERIMENTAL_API_USAGE")
fun CoroutinesStructure.toProbeFrame(): CoroutineProbeFrame {
    val root = buildCoroutineProbeFrame {
        name = "root"

        val probesTotally = structure.maxOf { it.probes.last().probeId }.toInt() + 1

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
    val states = this.probes.splitByStates(false, false)

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
                    threads = state.threads.toList()
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
internal class CoroutineStateRange private constructor(
    val coroutineId: Long,
    val state: State,
    val lastStackTrace: List<String> = emptyList(),
    val probesRange: IntRange,
    vararg val threads: String,
) {

    companion object {
        internal fun List<CoroutineProbe>.splitByStates(
            splitStatesIfHaveAnotherStateBetween: Boolean,
            differentThreadsInitiateDifferentFrames: Boolean
        ): List<CoroutineStateRange> {
            val split = this.getSplit(splitStatesIfHaveAnotherStateBetween, differentThreadsInitiateDifferentFrames)
                .map { range ->
                    CoroutineStateRange(
                        range.first().coroutineId,
                        range.first().state,
                        range.first().lastUpdatedStackTrace,
                        range.first().probeId..range.last().probeId,
                        *range.mapNotNull { it.lastUpdatedThreadName }.toSet().toTypedArray()
                    )
                }

            return split
        }
    }

}

private fun List<CoroutineProbe>.getSplit(
    splitStatesIfHaveAnotherStateBetween: Boolean,
    differentThreadsInitiateDifferentFrames: Boolean
): List<List<CoroutineProbe>> {
    val statesList = mutableListOf<List<CoroutineProbe>>()

    if (!splitStatesIfHaveAnotherStateBetween) {
        return groupBy { ProbesStateGroupKey.fromDelegate(it, differentThreadsInitiateDifferentFrames) }.values.toList()
    }


    val splitEdges = filterIndexed { index, coroutineProbe ->
        if (index > 0) {
            coroutineProbe.coroutineId != this[index - 1].coroutineId
                    || coroutineProbe.state != this[index - 1].state
                    || coroutineProbe.lastUpdatedStackTrace != this[index - 1].lastUpdatedStackTrace
                    || if (differentThreadsInitiateDifferentFrames) coroutineProbe.lastUpdatedThreadName != this[index - 1].lastUpdatedThreadName else false

        } else true
    }

    for (i in splitEdges.indices) {
        if (i > 0) {
            statesList.add(this.subList(indexOf(splitEdges[i - 1]), indexOf(splitEdges[i])))
        }
    }
    statesList.add(this.subList(indexOf(splitEdges.last()), this.lastIndex))

    return statesList
}

internal fun ProfilingCoroutineInfo.toThreadsFrame(): CoroutineThreadsFrame =
    buildCoroutineThreadsFrame {
        name = "root"

        this@toThreadsFrame.probes.filter { it.state == State.RUNNING }.groupingBy { it.lastUpdatedThreadName }
            .eachCount().forEach { thread ->
                value += thread.value
                addChild {
                    buildCoroutineThreadsFrame {
                        name = thread.key!!
                        value = thread.value
                    }
                }
            }
    }

private data class ProbesStateGroupKey(
    val coroutineId: Long,
    val coroutineState: State,
    val stackTrace: List<String>,
    val thread: String?,
) {
    companion object {
        fun fromDelegate(
            delegate: CoroutineProbe,
            differentThreadsInitiateDifferentFrames: Boolean
        ): ProbesStateGroupKey = ProbesStateGroupKey(
            delegate.coroutineId,
            delegate.state,
            delegate.lastUpdatedStackTrace,
            if (differentThreadsInitiateDifferentFrames) delegate.lastUpdatedThreadName else null
        )
    }
}