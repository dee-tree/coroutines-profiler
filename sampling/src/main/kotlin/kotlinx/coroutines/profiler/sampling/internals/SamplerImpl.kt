package kotlinx.coroutines.profiler.sampling.internals

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.sampling.statistics.ProfilingInternalStatistics
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalCoroutinesApi
internal class SamplerImpl : Sampler() {

    override fun probe(onNewCoroutineFound: (ProfilingCoroutineInfo) -> Unit): ProfilingCoroutineDump {
        val dump = dumpCoroutinesInfo() // Be careful: calling dumpCoroutinesInfo() updates probeID
        return dump.transform(onNewCoroutineFound)
    }

    @ExperimentalSerializationApi
    override fun getInternalStatistics(): ProfilingInternalStatistics.Builder? = null
}
