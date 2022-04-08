package kotlinx.coroutines.profiler.show

import kotlinx.serialization.SerialInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient
import kotlin.jvm.JvmName

@kotlinx.serialization.Serializable
data class SampleFrame(
    val name: String? = null,
    val value: Int,
    val children: List<SampleFrame>? = null,

    @SerialName("id")
    val coroutineId: Long? = null,
    @SerialName("state")
    val coroutineState: String? = null,
    val samples: Int,
    val stacktrace: List<String>,
    val thread: String? = null,
) {

//    val _children: MutableList<SampleFrame>? = childrenInitially?.toMutableList()

//    @kotlinx.serialization.Serializable
//    @SerialName("children")
//    val children by lazy { childrenInitially?.toMutableList() }
//        get(): List<SampleFrame>

//    private val _children: MutableList<SampleFrame>? = childrenInitially?.toMutableList()
//
//    val children: List<SampleFrame>?
//        get() = _children

//    fun wrap(other: SampleFrame) {
//        _children?.add(other)
//    }


}

inline fun buildSampleFrame(builderAction: SampleFrameBuilder.() -> Unit): SampleFrame {
    val builder = SampleFrameBuilder()
    builder.builderAction()
    return builder.build()
}

class SampleFrameBuilder @PublishedApi internal constructor() {
    var name: String? = null
    var value: Int = 0
    var samples: Int = 0

    private var childrenInitially: MutableList<SampleFrame>? = null

    var coroutineId: Long? = null
    var coroutineState: String? = null
    var stacktrace: List<String> = listOf()
    var thread: String? = null

    fun addChild(builderAction: SampleFrameBuilder.() -> SampleFrame) {
        childrenInitially ?: run { childrenInitially = mutableListOf() }
        childrenInitially!!.add(builderAction())
    }

    fun addChildren(builderAction: SampleFrameBuilder.() -> List<SampleFrame>) {
        childrenInitially ?: run { childrenInitially = mutableListOf() }
        val children = builderAction()
//        println("Children: ${children}")
        childrenInitially!!.addAll(children)
    }

    fun build(): SampleFrame = SampleFrame(
        name,
        value,
        childrenInitially,
        coroutineId,
        coroutineState,
        samples,
        stacktrace,
        thread
    )
}
