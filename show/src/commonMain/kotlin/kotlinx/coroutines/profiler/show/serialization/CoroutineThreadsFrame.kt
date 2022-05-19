package kotlinx.coroutines.profiler.show.serialization

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CoroutineThreadsFrame(
    val name: String,
    val value: Int,
    val children: List<CoroutineThreadsFrame>? = null,

    @SerialName("id")
    val coroutineId: Long
) {


}

inline fun buildCoroutineThreadsFrame(builderAction: CoroutineThreadsFrameBuilder.() -> Unit): CoroutineThreadsFrame {
    val builder = CoroutineThreadsFrameBuilder()
    builder.builderAction()
    return builder.build()
}



class CoroutineThreadsFrameBuilder @PublishedApi internal constructor() {
    var name: String = ""
    var value: Int = 0

    private var childrenInitially: MutableList<CoroutineThreadsFrame>? = null

    var coroutineId: Long = -1

    fun addChild(builderAction: CoroutineThreadsFrameBuilder.() -> CoroutineThreadsFrame) {
        childrenInitially ?: run { childrenInitially = mutableListOf() }
        childrenInitially!!.add(builderAction())
    }

    fun addChildren(builderAction: CoroutineThreadsFrameBuilder.() -> List<CoroutineThreadsFrame>) {
        childrenInitially ?: run { childrenInitially = mutableListOf() }
        val children = builderAction()
        childrenInitially!!.addAll(children)
    }

    fun build(): CoroutineThreadsFrame = CoroutineThreadsFrame(
        name,
        value,
        childrenInitially,
        coroutineId,
    )
}
