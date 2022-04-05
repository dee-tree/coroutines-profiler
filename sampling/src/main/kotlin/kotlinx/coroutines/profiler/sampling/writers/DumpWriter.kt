package kotlinx.coroutines.profiler.sampling.writers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineSample

@ExperimentalCoroutinesApi
interface DumpWriter {
    fun dumpNewCoroutine(coroutine: ProfilingCoroutineInfo)
    fun dumpSamples(samples: List<ProfilingCoroutineSample>)
    fun stop()

    val compression: Compression?
}

enum class Compression {
    GZIP
}