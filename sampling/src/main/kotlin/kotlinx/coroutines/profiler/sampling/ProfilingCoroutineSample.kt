package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.CoroutineInfo
import kotlinx.coroutines.debug.State

@kotlinx.serialization.Serializable
@ExperimentalCoroutinesApi
data class ProfilingCoroutineSample(
    val dumpId: Long,
    val coroutineId: Long,
    val state: State,
    val currentThreadName: String?,
    val currentStackTrace: List<String>
) {

    companion object {
        fun fromCoroutineInfo(delegate: CoroutineInfo, dumpId: Long) = ProfilingCoroutineSample(
            dumpId,
            delegate.id,
            delegate.state,
            delegate.lastObservedThread?.name,
            delegate.lastObservedStackTrace().map { it.toString() }
        )
    }
}
