package kotlinx.coroutines.profiler.core.writers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.core.data.*
import kotlinx.coroutines.profiler.core.data.statistics.InternalProfilingStatistics
import kotlinx.coroutines.profiler.core.data.statistics.ProfilingStatistics
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.zip.GZIPOutputStream

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


    private val openedRanges = OpenedRanges(coroutinesProbesFile)

    override fun writeDump(dump: CoroutinesDump) {
        probesCount += dump.dump.size

        executor.execute {
            dump.dump.forEach {
                openedRanges.push(it)
            }
        }
    }

    override fun stop() {
        println("Profiler is stopping...")
        println("Write dumps at ${folder.absolutePath}")

        executor.execute {
            openedRanges.completeAllRanges()
        }

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

private class OpenedRanges(
    private val probesFile: File
) {
    /**
     * first probe for this state *to* last probeId
     */
    private val openedRanges: MutableMap<CoroutineProbe, Int> = mutableMapOf()

    fun push(probe: CoroutineProbe) {
        openedRanges.keys.find { probe.coroutineId == it.coroutineId }?.let { sameCoroIdProbe ->
            if (sameCoroIdProbe.equalsExcludingProbeId(probe))
                openedRanges[sameCoroIdProbe] = probe.probeId
            else {
                probesFile.appendBytes(
                    (sameCoroIdProbe to openedRanges[sameCoroIdProbe]!!).toProbesRange().encodeToByteArray()
                )
                openedRanges.remove(sameCoroIdProbe)
            }
        } ?: run {
            // if coroutine not found in ranges => create the range
            openedRanges[probe] = probe.probeId
        }
    }

    fun completeAllRanges() {
        openedRanges.forEach { (probe, lastProbeId) ->
            probesFile.appendBytes((probe to lastProbeId).toProbesRange().encodeToByteArray())
        }
        openedRanges.clear()
    }
}

private fun Pair<CoroutineProbe, Int>.toProbesRange(): CoroutineProbesRange = when (first.state) {
    State.CREATED -> CreatedCoroutineProbesRange(
        first.coroutineId,
        first.probeId,
        second
    )
    State.RUNNING -> RunningCoroutineProbesRange(
        first.coroutineId,
        first.lastUpdatedStackTrace,
        first.lastUpdatedThreadName ?: error("Thread is null on running coroutine #${first.coroutineId}"),
        first.probeId,
        second
    )
    State.SUSPENDED -> SuspendedCoroutineProbesRange(
        first.coroutineId,
        first.lastUpdatedStackTrace,
        first.probeId,
        second
    )
}


private fun CoroutineProbe.equalsExcludingProbeId(other: CoroutineProbe): Boolean =
    this.coroutineId == other.coroutineId
            && this.state == other.state
            && this.lastUpdatedStackTrace == other.lastUpdatedStackTrace
            && this.lastUpdatedThreadName == other.lastUpdatedThreadName
