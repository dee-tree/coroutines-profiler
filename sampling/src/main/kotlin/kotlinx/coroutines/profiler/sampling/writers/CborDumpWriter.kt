package kotlinx.coroutines.profiler.sampling.writers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineSample
import kotlinx.coroutines.profiler.sampling.ProfilingResults
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import java.io.File
import java.util.zip.GZIPOutputStream

@Deprecated("Ineffective, use ProtobufDumpWriter")
@ExperimentalCoroutinesApi
internal class CborDumpWriter(
    private val dumpFolder: File,
    override val compression: Compression?
    ) : DumpWriter {

    init {
        dumpFolder.mkdirs()
    }

    val dumpFile = File(dumpFolder, "profiling_results.cbor")


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

        val fos = dumpFile.outputStream()

        val outputStream = when (compression) {
            null -> fos
            Compression.GZIP -> GZIPOutputStream(fos)
        }

        outputStream.use {
            it.write(Cbor.encodeToByteArray(ProfilingResults(coroutines, samples)))
        }

        File(dumpFolder, "profiling_results_nocomp.cbor").writeBytes(Cbor.encodeToByteArray(coroutines))
    }


}