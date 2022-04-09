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
    val name: String? = null,
    val value: Int,
    val children: List<CoroutineProbeFrame>? = null,

    @SerialName("id")
    val coroutineId: Long? = null,
    @SerialName("state")
    val coroutineState: String? = null,
    val probesCount: Int,
    val stacktrace: List<String>,
    val thread: String? = null,
) {

}

inline fun buildCoroutineProbeFrame(builderAction: CoroutineProbeFrameBuilder.() -> Unit): CoroutineProbeFrame {
    val builder = CoroutineProbeFrameBuilder()
    builder.builderAction()
    return builder.build()
}

class CoroutineProbeFrameBuilder @PublishedApi internal constructor() {
    var name: String? = null
    var value: Int = 0
    var probes: Int = 0

    private var childrenInitially: MutableList<CoroutineProbeFrame>? = null

    var coroutineId: Long? = null
    var coroutineState: String? = null
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
