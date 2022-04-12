package kotlinx.coroutines.profiler.core.data.statistics


@kotlinx.serialization.Serializable
data class ProfilingStatistics(
    val coroutinesCount: Int,
    val probesCount: Int,
    val specifiedProbesIntervalMillis: Int,
    val internalStatistics: InternalProfilingStatistics? = null
)
