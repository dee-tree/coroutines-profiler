package kotlinx.coroutines.profiler.show.serialization

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
    val coroutineState: String,
    val probesCount: Int,
    val stacktrace: List<String>,
    val thread: String? = null,
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
    var coroutineState: String = ""
    var stacktrace: List<String> = listOf()
    var thread: String? = null

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
        thread
    )
}
