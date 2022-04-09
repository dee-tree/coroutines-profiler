package kotlinx.coroutines.profiler.sampling.statistics

@kotlinx.serialization.Serializable
data class ProfilingInternalStatistics(
    val meanProbeTakingTimeMillis: Int,
    val maxProbeTakingTimeMillis: Int,

    val meanProbeHandlingTimeMillis: Int,
    val maxProbeHandlingTimeMillis: Int,
    val probeTakingQ1: Int, // quartile 1: percentile 25. 25% of elements is lower than this
    val probeTakingQ2: Int, // quartile 2: percentile 50. 50% of elements is lower than this
    val probeTakingQ3: Int  // quartile 3: percentile 75. 75% of elements is lower than this
) {

    class Builder internal constructor() {
        var meanProbeTakingTimeMillis: Int = -1
        var maxProbeTakingTimeMillis: Int = -1

        var meanProbeHandlingTimeMillis: Int = -1
        var maxProbeHandlingTimeMillis: Int = -1
        var probeTakingQ1: Int = -1
        var probeTakingQ2: Int = -1
        var probeTakingQ3: Int = -1

        fun build(): ProfilingInternalStatistics = ProfilingInternalStatistics(
            meanProbeTakingTimeMillis,
            maxProbeTakingTimeMillis,
            meanProbeHandlingTimeMillis,
            maxProbeHandlingTimeMillis,
            probeTakingQ1,
            probeTakingQ2,
            probeTakingQ3
        )

    }

    companion object {
        fun buildInternalStatistics(builderAction: Builder.() -> Unit): ProfilingInternalStatistics {
            val builder = Builder()
            builder.builderAction()
            return builder.build()
        }

        fun builder(): Builder = Builder()
        fun builder(builderAction: Builder.() -> Unit): Builder {
            val builder = Builder()
            builder.builderAction()
            return builder
        }
    }

}