package kotlinx.coroutines.profiler.core.data

@kotlinx.serialization.Serializable
data class CoroutineProbe(
    /**
     * id of probe when this sample was achieved
     */
    val probeId: Int,
    val coroutineId: Long,
    val state: State,
    val lastUpdatedThreadName: String?,
    /**
     * stacktrace on suspension and resumption points
     */
    val lastUpdatedStackTrace: List<String>
)
