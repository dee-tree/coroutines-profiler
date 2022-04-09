package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.CoroutineInfo
import kotlinx.coroutines.debug.State

@kotlinx.serialization.Serializable
@ExperimentalCoroutinesApi
data class ProfilingCoroutineProbe(
    /**
     * id of probe when this sample was achieved
     */
    val probeId: Int,
    val coroutineId: Long,
    val state: State,
    val currentThreadName: String?,
    /**
     * stacktrace on suspension and resumption points
     */
    val lastUpdatedStackTrace: List<String>
) {

    companion object {
        fun fromCoroutineInfo(delegate: CoroutineInfo, probeId: Int) = ProfilingCoroutineProbe(
            probeId,
            delegate.id,
            delegate.state,
            delegate.lastObservedThread?.name,
            delegate.lastObservedStackTrace().map { it.toString() }
        )
    }
}
