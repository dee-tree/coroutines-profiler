package kotlinx.coroutines.profiler.sampling.writers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineSample
import kotlinx.coroutines.profiler.sampling.ProfilingResults
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.zip.GZIPOutputStream

@ExperimentalSerializationApi
@ExperimentalCoroutinesApi
internal class ProtobufDumpWriter(
    private val dumpFolder: File,
    override val compression: Compression?,
    private val approximateSamplingIntervalMillis: Long
) : DumpWriter {

    val profilingResultsFile = File(dumpFolder, "profiling_results.json")
    val structureDumpFile = File(dumpFolder, "profiling_results_structure.proto")
    val samplesDumpFile = File(dumpFolder, "profiling_results_samples.proto")

    private val executor = Executors.newSingleThreadExecutor { action ->
        Thread(action, "dumps-writer").also { it.isDaemon = true }
    }

    init {
        dumpFolder.mkdirs()

        if (profilingResultsFile.exists()) profilingResultsFile.delete()
        if (structureDumpFile.exists()) structureDumpFile.delete()
        if (samplesDumpFile.exists()) samplesDumpFile.delete()
    }

    private var coroutinesCount: Int = 0
    private var samplesCount: Int = 0


    override fun dumpNewCoroutine(coroutine: ProfilingCoroutineInfo) {
        coroutinesCount++

        executor.execute {
            val encodedCoroutineInfo = ProtoBuf.encodeToByteArray(coroutine)

            structureDumpFile.appendBytes(encodedCoroutineInfo.size.toByteArray())
            structureDumpFile.appendBytes(encodedCoroutineInfo)
        }
    }

    override fun dumpSamples(samples: List<ProfilingCoroutineSample>) {
        samplesCount += samples.size

        executor.execute {
            samples.forEach { sample ->
                val encodedSample = ProtoBuf.encodeToByteArray(sample)

                samplesDumpFile.appendBytes(encodedSample.size.toByteArray())
                samplesDumpFile.appendBytes(encodedSample)
            }
        }
    }

    override fun stop() {
        println("Write dumps at ${dumpFolder.absolutePath}")

        executor.execute {
            compression?.let {
                structureDumpFile.compress(it)
                samplesDumpFile.compress(it)
            }
        }
        executor.shutdown()

        ProfilingResults.writeToFile(
            profilingResultsFile,
            structureDumpFile.absolutePath,
            samplesDumpFile.absolutePath,
            coroutinesCount,
            samplesCount,
            approximateSamplingIntervalMillis
        )

        executor.awaitTermination(1, TimeUnit.SECONDS)
    }

    fun File.compress(compression: Compression) {
        val content = readBytes()
        when (compression) {
            Compression.GZIP -> {
                val gzos = GZIPOutputStream(this.outputStream())
                gzos.write(content)
                gzos.close()
            }
        }
    }

}

private fun Int.toByteArray(): ByteArray = ByteBuffer.allocate(Int.SIZE_BYTES).putInt(this).array()
