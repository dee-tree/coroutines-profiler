package kotlinx.coroutines.profiler.core.writers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.core.data.*
import kotlinx.coroutines.profiler.core.data.statistics.InternalProfilingStatistics
import kotlinx.coroutines.profiler.core.data.statistics.ProfilingStatistics
import kotlinx.serialization.ExperimentalSerializationApi
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

    private var internalStatistics: InternalProfilingStatistics? = null

    override fun setInternalStatistics(stats: InternalProfilingStatistics) {
        internalStatistics = stats
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
            coroutinesStructureFile.appendBytes(coroutine.encodeToByteArray())
        }
    }

    override fun writeDump(dump: CoroutinesDump) {
        probesCount += dump.dump.size

        executor.execute {
            coroutinesProbesFile.appendBytes(dump.encodeToByteArray())
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
        println("Await all tasks execution by Executor")
        executor.awaitTermination(1, TimeUnit.HOURS)
        println("All tasks executed by Executor!")

        ProfilingResultFile(
            coroutinesStructureFile.absolutePath,
            coroutinesProbesFile.absolutePath,

            ProfilingStatistics(
                coroutinesCount,
                probesCount,
                specifiedProbesIntervalMillis,
                internalStatistics
            )
        ).writeToFile(profilingResultsFile)
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
