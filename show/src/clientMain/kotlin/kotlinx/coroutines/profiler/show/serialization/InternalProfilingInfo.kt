package kotlinx.coroutines.profiler.show.serialization

@kotlinx.serialization.Serializable
actual data class InternalProfilingInfo(
    actual val meanProbeTakingTimeMillis: Int,
    actual val maxProbeTakingTimeMillis: Int,
    actual val meanProbeHandlingTimeMillis: Int,
    actual val maxProbeHandlingTimeMillis: Int,
    actual val probeTakingQ1: Int, // quartile 1: percentile 25. 25% of elements is lower than this
    actual val probeTakingQ2: Int, // quartile 2: percentile 50. 50% of elements is lower than this
    actual val probeTakingQ3: Int  // quartile 3: percentile 75. 75% of elements is lower than this

)