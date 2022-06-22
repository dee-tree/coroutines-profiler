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
        coroutineState = State.CREATED

        val probesTotally = structure.maxOf { it.probes.maxOf { range -> range.toProbeId } }
            .toInt() + 1  // +1 because the first id is 0

        value = probesTotally

        structure.forEach { rootCoroutine ->
            addChildren {
                val list = rootCoroutine.toProbeFrames(1.0 / structure.size, listOf(0 until probesTotally))
                list
            }
        }
    }
    return root
}

@Suppress("EXPERIMENTAL_API_USAGE")
private fun StructuredProfilingCoroutineInfo.toProbeFrames(
    parentCompensationCoefficient: Double,
    parentDumpsIds: List<IntRange>
): List<CoroutineProbeFrame> {
    val states = this.probes.splitByStates(false, false)

    return buildList {
        states.forEach { state ->
            val probesForThisParentState =
                state.ranges.sumOf { range -> parentDumpsIds.sumOf { parentRange -> range.intersect(parentRange).size } } // state.probesRange.intersect(parentDumpsIds).size
            val width = (probesForThisParentState * parentCompensationCoefficient).toInt()
            if (width > 0) {

                val child = buildCoroutineProbeFrame {
                    name = "${this@toProbeFrames.name} ${this@toProbeFrames.id}"
                    coroutineId = this@toProbeFrames.id
                    coroutineState = state.state
                    probes = probesForThisParentState
                    stacktrace = state.lastStackTrace
                    threads = state.threads.toList()
                    value = width
                    probesRangeId = state.rangeId


                    this@toProbeFrames.children.forEach {
                        addChildren {
                            it.toProbeFrames(1.0 / children.size, state.ranges)
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
    val ranges: List<IntRange>,
    val rangeId: Int,
    vararg val threads: String,
) {

    companion object {
        internal fun List<CoroutineProbesRange>.splitByStates(
            splitStatesIfHaveAnotherStateBetween: Boolean,
            differentThreadsInitiateDifferentFrames: Boolean
        ): List<CoroutineStateRange> {
            val split = this.getSplit(splitStatesIfHaveAnotherStateBetween, differentThreadsInitiateDifferentFrames)
                .map { range ->
                    CoroutineStateRange(
                        range.first().coroutineId,
                        range.first().state,
                        range.first().lastSuspensionPointStackTrace,
                        range.map { it.probesRange },
                        range.first().rangeId,
                        *range.filterIsInstance<RunningCoroutineProbesRange>().map { it.thread }.toSet().toTypedArray()
                    )
                }

            return split
        }
    }

}

private fun List<CoroutineProbesRange>.getSplit(
    splitStatesIfHaveAnotherStateBetween: Boolean,
    differentThreadsInitiateDifferentFrames: Boolean
): List<List<CoroutineProbesRange>> {
    val statesList = mutableListOf<List<CoroutineProbesRange>>()

    if (!splitStatesIfHaveAnotherStateBetween) {
        return groupBy { ProbesStateGroupKey.fromDelegate(it, differentThreadsInitiateDifferentFrames) }.values.toList()
    }


    val splitEdges = filterIndexed { index, coroutineProbe ->
        if (index > 0) {
            coroutineProbe.coroutineId != this[index - 1].coroutineId
                    || coroutineProbe.state != this[index - 1].state
                    || coroutineProbe.lastSuspensionPointStackTrace != this[index - 1].lastSuspensionPointStackTrace
                    || if (differentThreadsInitiateDifferentFrames
                && coroutineProbe is RunningCoroutineProbesRange && this[index - 1] is RunningCoroutineProbesRange
            )
                coroutineProbe.thread != (this[index - 1] as RunningCoroutineProbesRange).thread else false

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

        this@toThreadsFrame.probes.filterIsInstance<RunningCoroutineProbesRange>().groupingBy { it.thread }
            .eachCount().forEach { thread ->
                value += thread.value
                addChild {
                    buildCoroutineThreadsFrame {
                        name = thread.key
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
            delegate: CoroutineProbesRange,
            differentThreadsInitiateDifferentFrames: Boolean
        ): ProbesStateGroupKey = ProbesStateGroupKey(
            delegate.coroutineId,
            delegate.state,
            delegate.lastSuspensionPointStackTrace,
            if (differentThreadsInitiateDifferentFrames && delegate is RunningCoroutineProbesRange) delegate.thread else null
        )
    }
}