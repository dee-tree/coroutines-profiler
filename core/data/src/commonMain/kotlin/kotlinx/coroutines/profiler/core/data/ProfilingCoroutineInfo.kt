package kotlinx.coroutines.profiler.core.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient


@kotlinx.serialization.Serializable
open class ProfilingCoroutineInfo(
    @SerialName("id")
    open val id: Long,
    @SerialName("name")
    open val name: String,
    @SerialName("parentId")
    open val parentId: Long?,
    @SerialName("creationStackTrace")
    open val creationStackTrace: List<String>,

    @Transient
    protected val _probes: MutableList<CoroutineProbesRange> = mutableListOf()
) {

    val probes: List<CoroutineProbesRange>
        get() = _probes


    fun setProbes(probes: List<CoroutineProbesRange>): ProfilingCoroutineInfo =
        this.apply {
            _probes.clear()
            _probes.addAll(probes)
        }

    open fun toStructured(children: List<ProfilingCoroutineInfo> = emptyList()): StructuredProfilingCoroutineInfo =
        StructuredProfilingCoroutineInfo(
            id, name, parentId, creationStackTrace,
            children.map {
                it.toStructured(emptyList())
            }, _probes
        )

    override fun toString(): String {
        return "Coroutine(id=${id}, parent:${parentId})"
    }

    companion object {
        fun LinearCoroutinesStructure.addProbes(
            probes: List<CoroutineProbesRange>
        ): LinearCoroutinesStructure {
            probes.forEach { probe ->
                this.coroutines.find {
                    probe.coroutineId == it.id
                }!!._probes.add(probe)
            }
            return this
        }
    }
}


open class StructuredProfilingCoroutineInfo(
    id: Long,
    name: String,
    parentId: Long?,
    creationStackTrace: List<String>,
    _children: List<StructuredProfilingCoroutineInfo>,
    _probes: List<CoroutineProbesRange> = emptyList()
) : ProfilingCoroutineInfo(id, name, parentId, creationStackTrace, _probes.toMutableList()) {

    private val _children: MutableList<StructuredProfilingCoroutineInfo> = _children.toMutableList()
    val children: List<StructuredProfilingCoroutineInfo>
        get() = _children

    operator fun get(coroutineId: Long): StructuredProfilingCoroutineInfo? = find { it.id == coroutineId }

    fun find(condition: (StructuredProfilingCoroutineInfo) -> Boolean): StructuredProfilingCoroutineInfo? {
        if (condition(this)) return this

        children.forEach { child ->
            child.find(condition)?.let { return it }
        }

        return null
    }

    fun walk(action: (StructuredProfilingCoroutineInfo) -> Unit) {
        action(this)

        children.forEach { child ->
            child.walk(action)
        }
    }

    fun addProbes(
        probes: List<CoroutineProbesRange>
    ): StructuredProfilingCoroutineInfo {
        probes.forEach { probe ->
            this.find {
                probe.coroutineId == it.id
            }!!._probes.add(probe)
        }
        return this
    }

    companion object {
        fun LinearCoroutinesStructure.toStructured(): CoroutinesStructure {
            val rootCoroutines = mutableListOf<StructuredProfilingCoroutineInfo>()
            val coroutineById = mutableMapOf<Long, StructuredProfilingCoroutineInfo>()

            this.coroutines.forEach { coroutine ->
                val structuredCoroutine = coroutine.toStructured()
                val parent = coroutineById[structuredCoroutine.parentId]

                if (parent == null) {
                    rootCoroutines.add(structuredCoroutine)
                } else {
                    parent._children.add(structuredCoroutine)
                }

                coroutineById[structuredCoroutine.id] = structuredCoroutine
            }
            return CoroutinesStructure(rootCoroutines)
        }

        fun CoroutinesStructure.addProbes(
            probes: List<CoroutineProbesRange>
        ): CoroutinesStructure {
            probes.forEach { probe ->
                this.find {
                    probe.coroutineId == it.id
                }!!._probes.add(probe)
            }
            return this
        }

    }


    override fun toString(): String {
        return "Coroutine(id=${id}, parent:${parentId}, children=${children})"
    }

}


fun CoroutinesStructure.find(condition: (StructuredProfilingCoroutineInfo) -> Boolean): StructuredProfilingCoroutineInfo? {

    structure.forEach { info ->
        info.find(condition)?.let { return it }
    }
    return null
}