package kotlinx.coroutines.profiler.core.data

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
sealed class ProfilingCoroutineInfo {
    @SerialName("id")
    abstract val id: Long

    @SerialName("name")
    abstract val name: String

    @SerialName("parentId")
    abstract val parentId: Long?

    @SerialName("creationStackTrace")
    abstract val creationStackTrace: List<String>
}

interface WithProbes {
    val probes: List<CoroutineProbe>
}

interface Structured {
    val children: List<Structured>

    fun walk(action: (Structured) -> Unit)
    operator fun get(coroutineId: Long): Structured?
}


@kotlinx.serialization.Serializable
open class ProfilingCoroutineInfoImpl(
    override val id: Long,
    override val name: String,
    override val parentId: Long?,
    override val creationStackTrace: List<String>
) : ProfilingCoroutineInfo() {

    fun withProbes(probes: List<CoroutineProbe>): ProfilingCoroutineInfoImplWithProbes = ProfilingCoroutineInfoImplWithProbes(
        id, name, parentId, creationStackTrace, probes.filter { it.coroutineId == id }
    )

    fun toStructured(children: List<ProfilingCoroutineInfoImpl> = emptyList()): StructuredProfilingCoroutineInfo = StructuredProfilingCoroutineInfo(
        id, name, parentId, creationStackTrace,
        children.map { StructuredProfilingCoroutineInfo(it.id, it.name, it.parentId, it.creationStackTrace, emptyList()) }
    )

}

class ProfilingCoroutineInfoImplWithProbes(
    override val id: Long,
    override val name: String,
    override val parentId: Long?,
    override val creationStackTrace: List<String>,
    override val probes: List<CoroutineProbe>
) : ProfilingCoroutineInfoImpl(id, name, parentId, creationStackTrace), WithProbes {

    fun toStructured(children: List<ProfilingCoroutineInfoImplWithProbes> = emptyList()): StructuredProfilingCoroutineInfoWithProbes =
        StructuredProfilingCoroutineInfoWithProbes(
            id, name, parentId, creationStackTrace,
            children.map {
                StructuredProfilingCoroutineInfoWithProbes(
                    it.id, it.name, it.parentId, it.creationStackTrace, emptyList(), it.probes
                )
            },
            probes
        )
}


open class StructuredProfilingCoroutineInfo(
    override val id: Long,
    override val name: String,
    override val parentId: Long?,
    override val creationStackTrace: List<String>,
    children: List<StructuredProfilingCoroutineInfo>
) : ProfilingCoroutineInfo(), Structured {

    private var _children: MutableList<StructuredProfilingCoroutineInfo> = children.toMutableList()
    override val children: List<StructuredProfilingCoroutineInfo>
        get() = _children


    override operator fun get(coroutineId: Long): StructuredProfilingCoroutineInfo? = find { it.id == coroutineId }

    fun find(condition: (StructuredProfilingCoroutineInfo) -> Boolean): StructuredProfilingCoroutineInfo? {
        if (condition(this)) return this

        children.forEach { child ->
            child.find(condition)?.let { return it }
        }

        return null
    }

    override fun walk(action: (Structured) -> Unit) {
        action(this)

        children.forEach { child ->
            child.walk(action)
        }
    }

    companion object {
        fun toStructured(infos: List<ProfilingCoroutineInfoImpl>): List<StructuredProfilingCoroutineInfo> {
            val rootCoroutines = mutableListOf<StructuredProfilingCoroutineInfo>()
            val coroutineById = mutableMapOf<Long, StructuredProfilingCoroutineInfo>()

            infos.forEach { coroutine ->
                val structuredCoroutine = coroutine.toStructured()
                val parent = coroutineById[structuredCoroutine.parentId]

                if (parent == null) {
                    rootCoroutines.add(structuredCoroutine)
                } else {
                    parent._children.add(structuredCoroutine)
                }

                coroutineById[coroutine.id] = structuredCoroutine
            }
            return rootCoroutines
        }
    }




    fun withProbes(probes: List<CoroutineProbe>): StructuredProfilingCoroutineInfoWithProbes {
        return StructuredProfilingCoroutineInfoWithProbes.fromStructuredCoroutineInfo(this, probes)
    }

}


class StructuredProfilingCoroutineInfoWithProbes(
    override val id: Long,
    override val name: String,
    override val parentId: Long?,
    override val creationStackTrace: List<String>,
    children: List<StructuredProfilingCoroutineInfoWithProbes>,
    probes: List<CoroutineProbe>
) : StructuredProfilingCoroutineInfo(id, name, parentId, creationStackTrace, children),
    Structured,
    WithProbes
{

    override fun get(coroutineId: Long): StructuredProfilingCoroutineInfoWithProbes? = super.get(coroutineId) as? StructuredProfilingCoroutineInfoWithProbes

    private var _children: MutableList<StructuredProfilingCoroutineInfoWithProbes> = children.toMutableList()
    override val children: List<StructuredProfilingCoroutineInfoWithProbes>
        get() = _children

    private var _probes: MutableList<CoroutineProbe> = probes.toMutableList()
    override val probes: List<CoroutineProbe>
        get() = _probes


    companion object {
        fun fromStructuredCoroutineInfo(info: StructuredProfilingCoroutineInfo, probes: List<CoroutineProbe>): StructuredProfilingCoroutineInfoWithProbes {
            val infoWithProbes = info.toWithProbes()

            probes.forEach { probe ->
                infoWithProbes[probe.coroutineId]!!._probes.add(probe)
            }
            return infoWithProbes
        }
    }
}


private fun StructuredProfilingCoroutineInfo.toWithProbes(): StructuredProfilingCoroutineInfoWithProbes = StructuredProfilingCoroutineInfoWithProbes(
    id, name, parentId, creationStackTrace, children.map { it.toWithProbes() }, emptyList()
)



//
//@kotlinx.serialization.Serializable
//class ProfilingCoroutineInfo internal constructor(
//    val id: Long,
//    val parentId: Long?,
//    val creationStackTrace: List<String>,
//    val name: String? = "unknown"
//) {
//
//    @Transient
//    private val _probes = mutableListOf<CoroutineProbe>()
//
//    @Transient
//    val probes: List<CoroutineProbe> = _probes
//
//    @Transient
//    private val _children = mutableListOf<ProfilingCoroutineInfo>()
//
//    @Transient
//    val children: List<ProfilingCoroutineInfo> = _children
//
//    private fun addChild(childCoroutine: ProfilingCoroutineInfo) {
//        _children.add(childCoroutine)
//    }
//
//    private fun addProbe(probe: CoroutineProbe) {
//        _probes.add(probe)
//    }
//
//    fun walk(action: (ProfilingCoroutineInfo) -> Unit) {
//        action(this)
//        children.forEach {
//            it.walk(action)
//        }
//    }
//
//
//    private fun asString(indent: Int = 0, indentWide: Int = 2): String = buildString {
//        if (indent > 0) {
//            append((" ".repeat(indentWide + 2) + "│").repeat(indent / indentWide - 1))
//            append((" ".repeat(indentWide + 2) + "├"))
//            append("─".repeat(indentWide) + " ")
//        }
//
//        appendLine("Coroutine(id: ${id}, parent: ${parentId})")
//
//        children.forEach {
//            append(it.asString(indent + indentWide))
//        }
//    }
//
//    override fun toString(): String = asString(0)
//
//    companion object {
//        fun Probes.bindWithInfos(
//            coroutines: CoroutinesStructure,
//        ): List<ProfilingCoroutineInfo> {
//            val rootCoroutines = mutableListOf<ProfilingCoroutineInfo>()
//            val coroutineById = mutableMapOf<Long, ProfilingCoroutineInfo>()
//
//            coroutines.structure.forEach { coroutine ->
//                val parent = coroutineById[coroutine.parentId]
//
//                if (parent == null) {
//                    rootCoroutines.add(coroutine)
//                } else {
//                    parent.addChild(coroutine)
//                }
//
//                coroutineById[coroutine.id] = coroutine
//            }
//
//            probes.forEach { probe ->
//                coroutineById[probe.coroutineId]!!.addProbe(probe)
//            }
//
//            return rootCoroutines
//        }
//    }
//}
