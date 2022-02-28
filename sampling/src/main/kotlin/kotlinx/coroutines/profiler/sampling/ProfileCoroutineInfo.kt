package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.debug.CoroutineInfo
import kotlinx.coroutines.profiler.sampling.postprocessing.CoroutineSamplesVisualizer
import kotlin.system.measureTimeMillis


@ExperimentalCoroutinesApi
class ProfileCoroutineInfo(private val coroutineInfo: CoroutineInfo) {

    val id get() = coroutineInfo.id

    private val _children = mutableListOf<ProfileCoroutineInfo>()
    val children: List<ProfileCoroutineInfo> = _children

    private val _samples = mutableListOf<CoroutineSample>(CoroutineSample(coroutineInfo))
    val samples: List<CoroutineSample> = _samples

    val creationStackTrace get() = coroutineInfo.creationStackTrace

    private var lastSampledJob: Job? = coroutineInfo.job


    private fun update(dumpedCoroutine: CoroutineInfo) {
        require(dumpedCoroutine.id == id) { "different coroutines!" }

        _samples.add(CoroutineSample(dumpedCoroutine))
        lastSampledJob = dumpedCoroutine.job
    }


    private fun Job.findConsideringChildren(condition: (Job) -> Boolean): Job? {
        if (condition(this)) return this
        children.toList().forEach {
            it.findConsideringChildren(condition)?.let { found -> return found }
        }

        return null
    }

    fun walk(action: (ProfileCoroutineInfo) -> Unit) {
        action(this)

        children.forEach {
            it.walk(action)
        }
    }

    private fun addChild(child: ProfileCoroutineInfo) {
        _children.add(child)
    }


    private fun asString(indent: Int = 0, indentWide: Int = 2): String = buildString {
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


    @ExperimentalCoroutinesApi
    internal class CoroutinesProfileInfoOwner {

        private val infoByJob = mutableMapOf<Job, ProfileCoroutineInfo>()

        private val coroutinesInfo = mutableListOf<ProfileCoroutineInfo>()

        private var coroutinesCount = 0

        internal fun sample(dump: List<CoroutineInfo>) {
            val existedNowCoroutinesInfo = mutableSetOf<ProfileCoroutineInfo>()

            dump.forEach { coroutineInfo ->
                val coroutineSampleTime = measureTimeMillis {
                    val foundInfo = infoByJob[coroutineInfo.job]

                    if (foundInfo == null) {
                        coroutinesCount++

                        val coroutineProfileInfo = ProfileCoroutineInfo(coroutineInfo)
                        val parentCoroutineInfo = coroutineProfileInfo.findParent()

                        parentCoroutineInfo?.addChild(coroutineProfileInfo) ?: coroutinesInfo.add(coroutineProfileInfo)

                        coroutineInfo.job?.let {
                            infoByJob[it] = coroutineProfileInfo
                        }

                        existedNowCoroutinesInfo.add(coroutineProfileInfo)

                    } else {
                        foundInfo.update(coroutineInfo)

                        existedNowCoroutinesInfo.add(foundInfo)
                    }

                }
                if (coroutineSampleTime > 5) println("Too long processing info: ${coroutineInfo}: $coroutineSampleTime ms")
            }

            infoByJob.entries.associate { (k, v) -> v to k }.minus(existedNowCoroutinesInfo)
                .onEach { it.key.lastSampledJob = null; infoByJob.remove(it.value) }
        }

        private fun ProfileCoroutineInfo.findParent(): ProfileCoroutineInfo? {
            coroutinesInfo.forEach { parent ->
                parent.lastSampledJob?.findConsideringChildren { parentJob ->
                    parentJob == this.lastSampledJob
                }?.let { found -> return parent }
            }
            return null
        }

        fun printReport() {

            println("Total count of coroutines were sampled: $coroutinesCount")
            coroutinesInfo.forEach(::println)
            println(); println()
        println("Threads & states info")

        coroutinesInfo.forEach { rootCoroutineInfo ->
            rootCoroutineInfo.walk {
                val visualizer = CoroutineSamplesVisualizer(it)
                println("Coroutine ${it.id}: ${visualizer.threadsInfo()}")
                println("Coroutine ${it.id}: ${visualizer.statesInfo()}")
            }
        }
        }
    }

}

