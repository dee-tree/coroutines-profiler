package kotlinx.coroutines.profiler.show.serialization

import kotlinx.coroutines.profiler.core.data.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.core.data.State
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CoroutineSuspensionsFrame(
    @SerialName("name")
    val stackFrame: String,
    @SerialName("value")
    private var _value: Int,
    @SerialName("children")
    val _children: MutableList<CoroutineSuspensionsFrame> = mutableListOf(),

    ) {
    val value: Int
        get() = _value

    val children: List<CoroutineSuspensionsFrame>
        get() = _children


    companion object {
        fun ProfilingCoroutineInfo.toCoroutineSuspensionsFrame(): CoroutineSuspensionsFrame {
            val rootFrame = CoroutineSuspensionsFrame("root", 0)

            this.probes.filter { it.state == State.SUSPENDED }.forEach { suspendProbe ->
                rootFrame.updateFrames(creationStackTrace.reversed() + suspendProbe.lastUpdatedStackTrace) {
                    it.value + 1
                }
            }
            return rootFrame
        }
    }

    private fun updateFrames(stackTrace: List<String>, getNewValue: (CoroutineSuspensionsFrame) -> Int) {
        var current = this

        stackTrace.forEach { stackFrame ->
            if (current.stackFrame != stackFrame)
                current = current.getOrCreate(stackFrame)
            current._value = getNewValue(current)

        }
    }

    /*
     * finds in children
     */
    fun getOrCreate(stackFrame: String): CoroutineSuspensionsFrame {
        return children
            .find { it.stackFrame == stackFrame }
            ?: (CoroutineSuspensionsFrame(stackFrame, 0).also { _children.add(it) })
    }
}