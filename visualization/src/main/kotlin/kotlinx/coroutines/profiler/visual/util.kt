@file:Suppress("EXPERIMENTAL_API_USAGE")

package kotlinx.coroutines.profiler.visual

import kotlinx.coroutines.debug.State
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo


internal fun ProfilingCoroutineInfo.totalExistenceSamples(): Int = samples.size
internal fun ProfilingCoroutineInfo.totalCreatedSamples(): Int = samples.filter { it.state == State.CREATED }.size
internal fun ProfilingCoroutineInfo.totalRunningSamples(): Int = samples.filter { it.state == State.RUNNING }.size
internal fun ProfilingCoroutineInfo.totalSuspendedSamples(): Int = samples.filter { it.state == State.SUSPENDED }.size

internal fun ProfilingCoroutineInfo.totalCreatedSamplesComparative(): String =
    "${(totalCreatedSamples() * 100) / totalExistenceSamples()}%"

internal fun ProfilingCoroutineInfo.totalRunningSamplesComparative(): String =
    "${(totalRunningSamples() * 100) / totalExistenceSamples()}%"

internal fun ProfilingCoroutineInfo.totalSuspendedSamplesComparative(): String =
    "${(totalSuspendedSamples() * 100) / totalExistenceSamples()}%"

internal fun ProfilingCoroutineInfo.threads(): Map<String, Int> {
    @Suppress("UNCHECKED_CAST")
    return samples.groupingBy { it.currentThreadName }.eachCount().filterKeys { it != null } as Map<String, Int>
}


class CoroutineStatesRange private constructor(
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
                    split.add(CoroutineStatesRange(it.state, it.currentThreadName, it.currentStackTrace, it.dumpId, it.dumpId))
                }
            }

            return split
        }
    }

}