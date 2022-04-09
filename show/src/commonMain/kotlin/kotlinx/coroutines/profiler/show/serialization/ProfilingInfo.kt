package kotlinx.coroutines.profiler.show.serialization

@kotlinx.serialization.Serializable
data class ProfilingInfo(
    val coroutinesCount: Int,
    val samplesCount: Int,
    val probesIntervalMillis: Int
)

expect class InternalProfilingInfo {
    val meanProbeTakingTimeMillis: Int
    val maxProbeTakingTimeMillis: Int
    val meanProbeHandlingTimeMillis: Int
    val maxProbeHandlingTimeMillis: Int
    val probeTakingQ1: Int // quartile 1: percentile 25. 25% of elements is lower than this
    val probeTakingQ2: Int // quartile 2: percentile 50. 50% of elements is lower than this
    val probeTakingQ3: Int // quartile 3: percentile 75. 75% of elements is lower than this

}


//@kotlinx.serialization.Serializable
//data class InternalProfilingInfo(
//    val meanProbeTakingTimeMillis: Int,
//    val maxProbeTakingTimeMillis: Int,
//    val meanProbeHandlingTimeMillis: Int,
//    val maxProbeHandlingTimeMillis: Int,
//    val probeTakingQ1: Int, // quartile 1: percentile 25. 25% of elements is lower than this
//    val probeTakingQ2: Int, // quartile 2: percentile 50. 50% of elements is lower than this
//    val probeTakingQ3: Int  // quartile 3: percentile 75. 75% of elements is lower than this
//)