package kotlinx.coroutines.profiler.show.serialization

import kotlinx.coroutines.profiler.core.data.CoroutinesStructure
import kotlinx.coroutines.profiler.core.data.LinearCoroutinesStructure
import kotlinx.coroutines.profiler.core.data.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.core.data.State
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

@kotlinx.serialization.Serializable
data class CoroutineSuspensionsFrame(
    @SerialName("name")
    val stackFrame: String,
    @SerialName("coroutineValues")
    private val coroutineValues: MutableMap<Long, Int> = mutableMapOf(),
    @SerialName("children")
    val _children: MutableList<CoroutineSuspensionsFrame> = mutableListOf(),
) {
    val value: Int
        get() = coroutineValues.values.sum()


    val children: List<CoroutineSuspensionsFrame>
        get() = _children


    companion object {

        fun CoroutineProbeFrame.toCoroutineSuspensionsFrame(coroutineInfo: ProfilingCoroutineInfo): CoroutineSuspensionsFrame {
            val root = CoroutineSuspensionsFrame("root")

            root.fillFromProbeFrame(this, coroutineInfo.creationStackTrace)

            return root
        }

        fun LinearCoroutinesStructure.toCoroutineSuspensionsFrame(): CoroutineSuspensionsFrame {
            val rootFrame = CoroutineSuspensionsFrame("root")
            coroutines.forEach {
                rootFrame.fillFromInfo(it)
            }
            return rootFrame
        }

        fun CoroutinesStructure.toCoroutineSuspensionsFrame(): CoroutineSuspensionsFrame {
            val rootFrame = CoroutineSuspensionsFrame("root")
            this.walk {
                rootFrame.fillFromInfo(it)
            }
            return rootFrame
        }

        fun ProfilingCoroutineInfo.toCoroutineSuspensionsFrame(): CoroutineSuspensionsFrame {
            val rootFrame = CoroutineSuspensionsFrame("root")
            rootFrame.fillFromInfo(this)
            return rootFrame
        }

        fun CoroutineSuspensionsFrame.asJsonValuedElement(): JsonElement {
            return Json.encodeToJsonElement(this.toFrameWithValue())
        }

        fun CoroutineSuspensionsFrame.toJsonString(): String {
            return Json.encodeToString(CoroutineSuspensionsFrameWithValue.serializer(), this.toFrameWithValue())
        }
    }

    private fun fillFromProbeFrame(probeFrame: CoroutineProbeFrame, creationStackTrace: List<String>) {
        updateFrames(
            probeFrame.coroutineId,
            creationStackTrace.reversed() + "CREATION STACKTRACE" + probeFrame.stacktrace.reversed(),
            probeFrame.probesCount
        )
    }

    private fun fillFromInfo(info: ProfilingCoroutineInfo) {
        info.probes.filter { it.state == State.SUSPENDED }.forEach { suspendProbe ->
            this.updateFrames(
                suspendProbe.coroutineId,
                info.creationStackTrace.reversed() + "CREATION STACKTRACE" + suspendProbe.lastSuspensionPointStackTrace.reversed()
            )
        }
    }


    private fun toFrameWithValue(): CoroutineSuspensionsFrameWithValue {
        return CoroutineSuspensionsFrameWithValue(
            stackFrame, value, coroutineValues,
            children.map { it.toFrameWithValue() }.toMutableList()
        )
    }

    private fun updateFrames(
        coroutineId: Long,
        stackTrace: List<String>,
        increment: Int = 1
    ) {
        var current = this

        stackTrace.forEach { stackFrame ->
            current.coroutineValues[coroutineId] = (current.coroutineValues[coroutineId] ?: 0) + increment
            if (current.stackFrame != stackFrame)
                current = current.getOrCreate(stackFrame)
        }

        // for last
        current.coroutineValues[coroutineId] = (current.coroutineValues[coroutineId] ?: 0) + increment
    }

    /*
     * finds in children
     */
    fun getOrCreate(stackFrame: String): CoroutineSuspensionsFrame {
        return children
            .find { it.stackFrame == stackFrame }
            ?: (CoroutineSuspensionsFrame(stackFrame).also { _children.add(it) })
    }


    @kotlinx.serialization.Serializable
    private class CoroutineSuspensionsFrameWithValue(
        @SerialName("name")
        val stackFrame: String,
        @SerialName("value")
        val value: Int,
        @SerialName("coroutineValues")
        private val coroutineValues: Map<Long, Int>,
        @SerialName("children")
        val _children: List<CoroutineSuspensionsFrameWithValue>
    )
}