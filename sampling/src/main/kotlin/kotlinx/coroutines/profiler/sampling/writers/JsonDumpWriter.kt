package kotlinx.coroutines.profiler.sampling.writers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineSample
import kotlinx.coroutines.profiler.sampling.ProfilingResults
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@ExperimentalCoroutinesApi
internal class JsonDumpWriter(private val dumpFolder: File) : DumpWriter {

    override val compression: Compression? = null

    init {
        dumpFolder.mkdirs()
    }

    val dumpFile = File(dumpFolder, "profiling_results.json")


    private val coroutines = mutableListOf<ProfilingCoroutineInfo>()
    private val samples = mutableListOf<ProfilingCoroutineSample>()

    override fun dumpNewCoroutine(coroutine: ProfilingCoroutineInfo) {
        coroutines.add(coroutine)
    }

    override fun dumpSamples(samples: List<ProfilingCoroutineSample>) {
        this.samples.addAll(samples)
    }

    override fun stop() {
        println("Write dumps at ${dumpFolder.absolutePath}")
        dumpFile.writeText(Json.encodeToString(ProfilingResults(coroutines, samples)))
    }


}