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
    protected val _probes: MutableList<CoroutineProbe> = mutableListOf()
) {

    val probes: List<CoroutineProbe>
        get() = _probes


    fun setProbes(probes: List<CoroutineProbe>): ProfilingCoroutineInfo =
        this.apply {
            _probes.clear()
            _probes.addAll(probes)
        }

    open fun toStructured(children: List<ProfilingCoroutineInfo> = emptyList()): StructuredProfilingCoroutineInfo =
        StructuredProfilingCoroutineInfo(
            id, name, parentId, creationStackTrace,
            children.map {
                it.toStructured(emptyList())
//                StructuredProfilingCoroutineInfo(
//                    it.id,
//                    it.name,
//                    it.parentId,
//                    it.creationStackTrace,
//                    emptyList(),
//                    it._probes
//                )
            }, _probes
        )

    override fun toString(): String {
        return "Coroutine(id=${id}, parent:${parentId})"
    }
}

//class ProfilingCoroutineInfoWithProbes(
//    id: Long,
//    name: String,
//    parentId: Long?,
//    creationStackTrace: List<String>,
//    override val probes: List<CoroutineProbe>
//) : ProfilingCoroutineInfo(id, name, parentId, creationStackTrace), WithProbes {
//
//    fun toStructured(children: List<ProfilingCoroutineInfoWithProbes> = emptyList()): StructuredProfilingCoroutineInfoWithProbes =
//        StructuredProfilingCoroutineInfoWithProbes(
//            id, name, parentId, creationStackTrace,
//            children.map {
//                StructuredProfilingCoroutineInfoWithProbes(
//                    it.id, it.name, it.parentId, it.creationStackTrace, emptyList(), it.probes
//                )
//            },
//            probes
//        )
//}


open class StructuredProfilingCoroutineInfo(
    id: Long,
    name: String,
    parentId: Long?,
    creationStackTrace: List<String>,
    _children: List<StructuredProfilingCoroutineInfo>,
    _probes: List<CoroutineProbe> = emptyList()
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
        probes: List<CoroutineProbe>
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
            probes: List<CoroutineProbe>
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


//class StructuredProfilingCoroutineInfoWithProbes(
//    id: Long,
//    name: String,
//    parentId: Long?,
//    creationStackTrace: List<String>,
//    children: List<StructuredProfilingCoroutineInfoWithProbes>,
//    probes: List<CoroutineProbe>
//) : StructuredProfilingCoroutineInfo(id, name, parentId, creationStackTrace, children),
//    Structured,
//    WithProbes {
//
//
//    private var _children: MutableList<StructuredProfilingCoroutineInfoWithProbes> = children.toMutableList()
//    override val children: List<StructuredProfilingCoroutineInfoWithProbes>
//        get() = _children
//
//    private var _probes: MutableList<CoroutineProbe> = probes.toMutableList()
//    override val probes: List<CoroutineProbe>
//        get() = _probes
//
//
//
//    fun findWithProbes(condition: (StructuredProfilingCoroutineInfoWithProbes) -> Boolean): StructuredProfilingCoroutineInfoWithProbes? {
//        if (condition(this)) return this
//
//        children.forEach { child ->
//            child.findWithProbes(condition)?.let { return it }
//        }
//
//        return null
//    }
//
//    override operator fun get(coroutineId: Long): StructuredProfilingCoroutineInfoWithProbes? = this.findWithProbes { it.id == coroutineId }
//
//
//    companion object {
////        fun StructuredProfilingCoroutineInfofromStructuredCoroutineInfo(
////            info: StructuredProfilingCoroutineInfo,
////            probes: List<CoroutineProbe>
////        ): StructuredProfilingCoroutineInfoWithProbes {
////            val infoWithProbes = info.toWithProbes()
////
////            probes.forEach { probe ->
////                infoWithProbes[probe.coroutineId]!!._probes.add(probe)
////            }
////            return infoWithProbes
////        }
//
////        fun CoroutinesStructure.addProbes(
//////            infos: List<StructuredProfilingCoroutineInfo>,
////            probes: List<CoroutineProbe>
////        ): CoroutinesStructure {
////
//////            val infosWithProbes = this.structure.map { it.toWithProbes() }
////
////            probes.forEach { probe ->
////
////                this.structure.findInfo { probe.coroutineId == it.id }!!._probes.add(probe).also {
//////                    println("Add probe: ${probe} to info: ${infosWithProbes.find { it[probe.coroutineId] != null }}")
////                }
////            }
////            return infosWithProbes
////        }
//    }
//}


fun CoroutinesStructure.find(condition: (StructuredProfilingCoroutineInfo) -> Boolean): StructuredProfilingCoroutineInfo? {

    structure.forEach { info ->
        info.find(condition)?.let { return it }
    }
    return null
}