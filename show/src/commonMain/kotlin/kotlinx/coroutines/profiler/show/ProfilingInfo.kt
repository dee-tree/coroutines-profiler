package kotlinx.coroutines.profiler.show

@kotlinx.serialization.Serializable
data class ProfilingInfo(
    val coroutinesCount: Int,
    val samplesCount: Int,
    val samplesIntervalMillis: Long
)
