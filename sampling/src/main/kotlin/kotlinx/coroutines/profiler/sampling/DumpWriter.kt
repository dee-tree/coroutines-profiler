package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
interface DumpWriter {
    fun dumpNewCoroutine(coroutine: ProfilingCoroutineInfo)
    fun dumpSamples(samples: List<ProfilingCoroutineSample>)
    fun stop()
}