package kotlinx.coroutines.profiler.sampling.writers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.sampling.internals.ProfilingCoroutineDump
import kotlinx.coroutines.profiler.sampling.statistics.ProfilingInternalStatistics

@ExperimentalCoroutinesApi
interface DumpWriter {
    fun writeNewCoroutine(coroutine: ProfilingCoroutineInfo)
    fun writeDump(dump: ProfilingCoroutineDump)
    fun stop()

    val compression: Compression?

    fun setInternalStatistics(stats: ProfilingInternalStatistics)
}

enum class Compression {
    GZIP
}