package kotlinx.coroutines.profiler.core.data

data class CoroutinesDump(
    val probeId: Int,
    val dump: List<CoroutineProbe>
)