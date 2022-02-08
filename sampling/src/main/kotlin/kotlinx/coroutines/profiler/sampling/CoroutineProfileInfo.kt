package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.debug.CoroutineInfo
import kotlinx.coroutines.profiler.sampling.postprocessing.CoroutineSamplesVisualizer

@ExperimentalCoroutinesApi
class CoroutinesProfileInfoOwner {

    private val infoByJob = mutableMapOf<Job, CoroutineProfileInfo>()

    private val coroutinesInfo = mutableListOf<CoroutineProfileInfo>()

    fun sample(dump: List<CoroutineInfo>) {
        dump.forEach { dumpInfo ->

            val foundInfo = infoByJob[dumpInfo.job]

            if (foundInfo == null) {
                val coroutineProfileInfo = CoroutineProfileInfo(dumpInfo)
                val parentCoroutineInfo = coroutineProfileInfo.findParent()

                if (parentCoroutineInfo != null) {
                    parentCoroutineInfo.addChild(coroutineProfileInfo)
                } else {
                    coroutinesInfo.add(coroutineProfileInfo)
                }

                coroutineProfileInfo.lastSampledJob?.let {
                    infoByJob[it] = coroutineProfileInfo
                }

            } else {
                foundInfo.updateSample(dumpInfo)
            }

        }
    }

    private fun CoroutineProfileInfo.findParent(): CoroutineProfileInfo? {
        coroutinesInfo.forEach { parent ->
            parent.lastSampledJob?.findConsideringChildren { parentJob ->
                parentJob == this.lastSampledJob
            }?.let { found -> return parent }
        }
        return null
    }

    fun printReport() {
        coroutinesInfo.forEach(::println)
        println(); println()
        coroutinesInfo.forEach { rootCoroutineInfo ->
            rootCoroutineInfo.walk {
                println("${it.lastSampledJob}: ${CoroutineSamplesVisualizer(it).threadsInfo()}")
            }
        }
    }
}

@ExperimentalCoroutinesApi
class CoroutineProfileInfo(delegate: CoroutineInfo) {

    val creationStackTrace = delegate.creationStackTrace
    val id = delegate.id

    private val _children = mutableListOf<CoroutineProfileInfo>()
    val children: List<CoroutineProfileInfo> = _children

    private val _samples = mutableListOf<CoroutineSample>(CoroutineSample(delegate))
    val samples: List<CoroutineSample> = _samples

    var lastSampledJob: Job? = delegate.job

    fun addChild(child: CoroutineProfileInfo) {
        _children.add(child)
    }


    fun updateSample(dump: CoroutineInfo) {
        require(dump.id == id) { "different coroutines!" }

        lastSampledJob = dump.job
        _samples.add(CoroutineSample(dump))
    }


    fun Job.findConsideringChildren(condition: (Job) -> Boolean): Job? {
        if (condition(this)) return this

        children.toList().forEach {
            it.findConsideringChildren(condition)?.let { found -> return found }
        }

        return null
    }

    fun walk(action: (CoroutineProfileInfo) -> Unit) {
        action(this)

        children.forEach {
            it.walk(action)
        }
    }

    private val indentWide = 2

    private fun asString(indent: Int = 0): String = buildString {
        if (indent > 0) {
            appendLine((" ".repeat(indentWide + 2) + "|").repeat(indent / indentWide))
            append((" ".repeat(indentWide + 2) + "|").repeat(indent / indentWide))
            append("_".repeat(indentWide))
        }

        appendLine("Coroutine(id: $id, job: $lastSampledJob)")

        children.forEach {
            append(it.asString(indent + indentWide))
        }

    }

    override fun toString(): String = asString(0)
}

