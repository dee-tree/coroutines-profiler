package kotlinx.coroutines.profiler.show.serialization

import kotlinx.coroutines.profiler.core.data.State
import kotlinx.serialization.SerialName

/*
    * This class is used to represent stackframes for flamegraph
    * It must have:
        * name
        * value
        * another - to provide additional info
 */
@kotlinx.serialization.Serializable
data class CoroutineProbeFrame(
    val name: String,
    val value: Int,
    val children: List<CoroutineProbeFrame>? = null,

    @SerialName("id")
    val coroutineId: Long,
    @SerialName("state")
    val coroutineState: State,
    val probesCount: Int,
    val stacktrace: List<String>,
    val threads: List<String> = emptyList(),
    val probesRangeId: Int
) {

    fun walk(action: (CoroutineProbeFrame) -> Unit) {
        action(this)

        children?.forEach {
            it.walk(action)
        }

    }
}

inline fun buildCoroutineProbeFrame(builderAction: CoroutineProbeFrameBuilder.() -> Unit): CoroutineProbeFrame {
    val builder = CoroutineProbeFrameBuilder()
    builder.builderAction()
    return builder.build()
}

class CoroutineProbeFrameBuilder @PublishedApi internal constructor() {
    var name: String = ""
    var value: Int = 0
    var probes: Int = 0

    private var childrenInitially: MutableList<CoroutineProbeFrame>? = null

    var coroutineId: Long = -1
    lateinit var coroutineState: State
    var stacktrace: List<String> = listOf()
    var threads: List<String> = emptyList()
    var probesRangeId: Int = -1

    fun addChild(builderAction: CoroutineProbeFrameBuilder.() -> CoroutineProbeFrame) {
        childrenInitially ?: run { childrenInitially = mutableListOf() }
        childrenInitially!!.add(builderAction())
    }

    fun addChildren(builderAction: CoroutineProbeFrameBuilder.() -> List<CoroutineProbeFrame>) {
        childrenInitially ?: run { childrenInitially = mutableListOf() }
        val children = builderAction()
        childrenInitially!!.addAll(children)
    }

    fun build(): CoroutineProbeFrame = CoroutineProbeFrame(
        name,
        value,
        childrenInitially,
        coroutineId,
        coroutineState,
        probes,
        stacktrace,
        threads,
        probesRangeId
    )
}
