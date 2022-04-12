package kotlinx.coroutines.profiler.core.internals

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.core.data.CoroutinesDump
import kotlinx.coroutines.profiler.core.data.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.core.data.statistics.ProbeHandlingStatistics
import kotlinx.coroutines.profiler.core.data.statistics.ProbeTakingStatistics

@ExperimentalCoroutinesApi
internal class SamplerImpl : Sampler() {

    override fun probe(onNewCoroutineFound: (ProfilingCoroutineInfo) -> Unit): CoroutinesDump {
        val dump = dumpCoroutinesInfo() // Be careful: calling dumpCoroutinesInfo() updates probeID
        return dump.transform(onNewCoroutineFound)
    }

    override fun getHandlingStatistics(): ProbeHandlingStatistics? = null
    override fun getTakingStatistics(): ProbeTakingStatistics? = null
}
