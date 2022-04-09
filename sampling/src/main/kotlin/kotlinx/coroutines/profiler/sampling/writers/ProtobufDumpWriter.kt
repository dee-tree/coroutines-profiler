package kotlinx.coroutines.profiler.sampling.writers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.sampling.internals.ProfilingCoroutineDump
import kotlinx.coroutines.profiler.sampling.statistics.ProfilingInternalStatistics
import kotlinx.coroutines.profiler.sampling.statistics.ProfilingStatistics
import kotlinx.coroutines.profiler.sampling.toByteArray
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.zip.GZIPOutputStream

@ExperimentalSerializationApi
@ExperimentalCoroutinesApi
internal class ProtobufDumpWriter(
    private val folder: File,
    override val compression: Compression?,
    private val specifiedProbesIntervalMillis: Int
) : DumpWriter {

    val profilingResultsFile = File(folder, "coroprof.json")
    val coroutinesStructureFile = File(folder, "coroprof_struct.proto")
    val coroutinesProbesFile = File(folder, "coroprof_probes.proto")

    private var profilingInternalStats: ProfilingInternalStatistics? = null

    override fun setInternalStatistics(stats: ProfilingInternalStatistics) {
        profilingInternalStats = stats
    }

    private val executor = Executors.newSingleThreadExecutor { action ->
        Thread(action, "dumps-writer").also { it.isDaemon = true }
    }

    init {
        folder.mkdirs()

        if (profilingResultsFile.exists()) profilingResultsFile.delete()
        if (coroutinesStructureFile.exists()) coroutinesStructureFile.delete()
        if (coroutinesProbesFile.exists()) coroutinesProbesFile.delete()
    }

    private var coroutinesCount: Int = 0
    private var probesCount: Int = 0


    override fun writeNewCoroutine(coroutine: ProfilingCoroutineInfo) {
        coroutinesCount++

        executor.execute {
            val encodedCoroutineInfo = ProtoBuf.encodeToByteArray(coroutine)

            coroutinesStructureFile.appendBytes(encodedCoroutineInfo.size.toByteArray())
            coroutinesStructureFile.appendBytes(encodedCoroutineInfo)
        }
    }

    override fun writeDump(dump: ProfilingCoroutineDump) {
        probesCount += dump.dump.size

        executor.execute {
            dump.dump.forEach { probe ->
                val encodedProbe = ProtoBuf.encodeToByteArray(probe)

                coroutinesProbesFile.appendBytes(encodedProbe.size.toByteArray())
                coroutinesProbesFile.appendBytes(encodedProbe)
            }
        }
    }

    override fun stop() {
        println("Profiler is stopping...")
        println("Write dumps at ${folder.absolutePath}")

        executor.execute {
            compression?.let {
                coroutinesStructureFile.compress(it)
                coroutinesProbesFile.compress(it)
            }
        }
        executor.shutdown()

        ProfilingStatistics(
            coroutinesStructureFile.absolutePath,
            coroutinesProbesFile.absolutePath,
            coroutinesCount,
            probesCount,
            specifiedProbesIntervalMillis,
            profilingInternalStats
        ).writeToFile(profilingResultsFile)

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
