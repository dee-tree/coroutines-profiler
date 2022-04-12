package kotlinx.coroutines.profiler.core.data.statistics

@kotlinx.serialization.Serializable
data class InternalProfilingStatistics(
    val probeTakingStatistics: ProbeTakingStatistics,
    val probeHandlingStatistics: ProbeHandlingStatistics
)

@kotlinx.serialization.Serializable
data class ProbeTakingStatistics(
    val meanTimeMillis: Long,
    val maxTimeMillis: Long,

    val probeTakingQ1: Long, // quartile 1: percentile 25. 25% of elements is lower than this
    val probeTakingQ2: Long, // quartile 2: percentile 50. 50% of elements is lower than this
    val probeTakingQ3: Long  // quartile 3: percentile 75. 75% of elements is lower than this
)

@kotlinx.serialization.Serializable
data class ProbeHandlingStatistics(
    val meanProbeHandlingTimeMillis: Long,
    val maxProbeHandlingTimeMillis: Long,

    val probeHandlingQ1: Long, // quartile 1: percentile 25. 25% of elements is lower than this
    val probeHandlingQ2: Long, // quartile 2: percentile 50. 50% of elements is lower than this
    val probeHandlingQ3: Long  // quartile 3: percentile 75. 75% of elements is lower than this
)