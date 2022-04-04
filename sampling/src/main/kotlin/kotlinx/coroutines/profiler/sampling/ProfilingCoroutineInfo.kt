package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.Transient

@kotlinx.serialization.Serializable
@ExperimentalCoroutinesApi
data class ProfilingCoroutineInfo internal constructor(
    val id: Long,
    val parentId: Long?,
    val creationStackTrace: List<String>,
    val name: String? = "unknown"
) {

    @Transient
    private val _samples = mutableListOf<ProfilingCoroutineSample>()
    @Transient
    val samples: List<ProfilingCoroutineSample> = _samples

    @Transient
    private val _children = mutableListOf<ProfilingCoroutineInfo>()

    @Transient
    val children: List<ProfilingCoroutineInfo> = _children

    fun addChild(childCoroutine: ProfilingCoroutineInfo) {
        _children.add(childCoroutine)
    }

    fun addSample(sample: ProfilingCoroutineSample) {
        _samples.add(sample)
    }

    fun walk(action: (ProfilingCoroutineInfo) -> Unit) {
        action(this)
        children.forEach {
            it.walk(action)
        }
    }


    private fun asString(indent: Int = 0, indentWide: Int = 2): String = buildString {
        if (indent > 0) {
            append((" ".repeat(indentWide + 2) + "│").repeat(indent / indentWide - 1))
            append((" ".repeat(indentWide + 2) + "├"))
            append("─".repeat(indentWide) + " ")
        }

        appendLine("Coroutine(id: ${id}, parent: ${parentId})")

        children.forEach {
            append(it.asString(indent + indentWide))
        }
    }

    override fun toString(): String = asString(0)

    companion object {
        fun fromDump(
            coroutines: List<ProfilingCoroutineInfo>,
            samples: List<ProfilingCoroutineSample>
        ): List<ProfilingCoroutineInfo> {
            val rootCoroutines = mutableListOf<ProfilingCoroutineInfo>()
            val coroutineById = mutableMapOf<Long, ProfilingCoroutineInfo>()

            coroutines.forEach { coroutine ->
                val parent = coroutineById[coroutine.parentId]

                if (parent == null) {
                    rootCoroutines.add(coroutine)
                } else {
                    parent.addChild(coroutine)
                }

                coroutineById[coroutine.id] = coroutine
            }

            samples.forEach { sample ->
                coroutineById[sample.coroutineId]?.addSample(sample) ?: throw IllegalArgumentException("Hey dude! It's funny that sample for coroutine #${sample.coroutineId} was catched, but it doesn't exist... ")
            }

            return rootCoroutines
        }
    }
}
