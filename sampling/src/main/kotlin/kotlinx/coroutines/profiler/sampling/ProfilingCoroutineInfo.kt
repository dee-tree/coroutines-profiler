package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.sampling.data.CoroutinesProbes
import kotlinx.coroutines.profiler.sampling.data.CoroutinesStructure
import kotlinx.coroutines.profiler.sampling.internals.ProfilingCoroutineDump
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
    private val _probes = mutableListOf<ProfilingCoroutineProbe>()
    @Transient
    val probes: List<ProfilingCoroutineProbe> = _probes

    @Transient
    private val _children = mutableListOf<ProfilingCoroutineInfo>()

    @Transient
    val children: List<ProfilingCoroutineInfo> = _children

    internal fun addChild(childCoroutine: ProfilingCoroutineInfo) {
        _children.add(childCoroutine)
    }

    internal fun addProbe(probe: ProfilingCoroutineProbe) {
        _probes.add(probe)
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
        fun CoroutinesProbes.bindWithInfos(
            coroutines: CoroutinesStructure,
//            coroutines: List<ProfilingCoroutineInfo>,
        ): List<ProfilingCoroutineInfo> {
            val rootCoroutines = mutableListOf<ProfilingCoroutineInfo>()
            val coroutineById = mutableMapOf<Long, ProfilingCoroutineInfo>()

            coroutines.structure.forEach { coroutine ->
                val parent = coroutineById[coroutine.parentId]

                if (parent == null) {
                    rootCoroutines.add(coroutine)
                } else {
                    parent.addChild(coroutine)
                }

                coroutineById[coroutine.id] = coroutine
            }

            samples.forEach { probe ->
                coroutineById[probe.coroutineId]!!.addProbe(probe)
            }

            return rootCoroutines
        }
    }
}
