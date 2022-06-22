package kotlinx.coroutines.profiler.core.data

import kotlinx.serialization.Serializable

private var _nextRangeId = 0
private val nextRangeId
    get() = _nextRangeId++


@Serializable
sealed class CoroutineProbesRange {
    abstract val coroutineId: Long

    val state: State = when (this) {
        is CreatedCoroutineProbesRange -> State.CREATED
        is RunningCoroutineProbesRange -> State.RUNNING
        is SuspendedCoroutineProbesRange -> State.SUSPENDED
    }

    abstract val lastSuspensionPointStackTrace: List<String>

    /**
     * fromProbeId inclusive
     */
    abstract val fromProbeId: Int

    /**
     * toProbeId inclusive
     */
    abstract val toProbeId: Int

    abstract val rangeId: Int

    val probesRange
        get() = fromProbeId..toProbeId
}


@Serializable
data class SuspendedCoroutineProbesRange(
    override val coroutineId: Long,
    val suspensionPointStackTrace: List<String>,
    override val fromProbeId: Int,
    override val toProbeId: Int
) : CoroutineProbesRange() {

    override val lastSuspensionPointStackTrace: List<String>
        get() = suspensionPointStackTrace

    override val rangeId: Int = nextRangeId

    override fun toString(): String {
        return "CreatedCoroutineProbesRange(coroutineId=${coroutineId}, probesRange=${probesRange}, suspensionPointTrace=${suspensionPointStackTrace}, rangeId=${rangeId})"
    }
}

@Serializable
data class RunningCoroutineProbesRange(
    override val coroutineId: Long,
    override val lastSuspensionPointStackTrace: List<String>,
    val thread: String,

    override val fromProbeId: Int,
    override val toProbeId: Int
) : CoroutineProbesRange() {

    override val rangeId: Int = nextRangeId

    override fun toString(): String {
        return "RunningCoroutineProbesRange(coroutineId=${coroutineId}, probesRange=${probesRange}, thread=${thread}, lastSuspensionTrace=${lastSuspensionPointStackTrace}, rangeId=${rangeId})"
    }
}

@Serializable
data class CreatedCoroutineProbesRange(
    override val coroutineId: Long,

    override val fromProbeId: Int,
    override val toProbeId: Int
) : CoroutineProbesRange() {
    override val lastSuspensionPointStackTrace: List<String> = emptyList()

    override val rangeId: Int = nextRangeId

    override fun toString(): String {
        return "CreatedCoroutineProbesRange(coroutineId=${coroutineId}, probesRange=${probesRange}, rangeId=${rangeId})"
    }
}
