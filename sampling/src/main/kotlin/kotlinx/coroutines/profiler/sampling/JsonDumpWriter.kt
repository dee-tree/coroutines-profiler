package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ExperimentalCoroutinesApi
internal class JsonDumpWriter(
    directory: File,
    createSubDirWithTime: Boolean = true
) : DumpWriter {

    private val dumpFolder = (if (createSubDirWithTime) File(directory, nowFormatted()) else directory).apply { mkdirs() }

    val coroutinesListFile = File(dumpFolder, "coro-profiling-all.json")
    val samplesFile = File(dumpFolder, "coro-profiling-samples.json")


    private val coroutines = mutableListOf<ProfilingCoroutineInfo>()
    private val samples = mutableListOf<ProfilingCoroutineSample>()

    override fun dumpNewCoroutine(coroutine: ProfilingCoroutineInfo) {
        coroutines.add(coroutine)
    }

    override fun dumpSamples(samples: List<ProfilingCoroutineSample>) {
        this.samples.addAll(samples)
    }

    override fun stop() {
        println("Dump folder: ${dumpFolder}")
        coroutinesListFile.writeText(Json.encodeToString(coroutines.toList()))
        samplesFile.writeText(Json.encodeToString(samples.toList()))
    }

    private fun nowFormatted() = DateTimeFormatter.ofPattern("yyyy_MM_dd-hh_mm_ss").format(LocalDateTime.now())
}