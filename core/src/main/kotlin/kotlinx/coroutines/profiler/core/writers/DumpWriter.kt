package kotlinx.coroutines.profiler.core.writers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.core.data.CoroutinesDump
import kotlinx.coroutines.profiler.core.data.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.core.data.statistics.InternalProfilingStatistics

@ExperimentalCoroutinesApi
interface DumpWriter {
    fun writeNewCoroutine(coroutine: ProfilingCoroutineInfo)
    fun writeDump(dump: CoroutinesDump)
    fun stop()

    val compression: Compression?

    fun setInternalStatistics(stats: InternalProfilingStatistics)
}

enum class Compression {
    GZIP
}