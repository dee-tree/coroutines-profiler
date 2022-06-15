package kotlinx.coroutines.profiler.core.internals

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.core.data.CoroutinesDump
import kotlinx.coroutines.profiler.core.data.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.core.data.statistics.ProbeHandlingStatistics
import kotlinx.coroutines.profiler.core.data.statistics.ProbeTakingStatistics
import kotlinx.coroutines.profiler.core.internals.statistics.ProbeHandlingStatisticsCollector
import kotlinx.coroutines.profiler.core.internals.statistics.ProbeTakingStatisticsCollector
import kotlin.contracts.ExperimentalContracts
import kotlin.time.ExperimentalTime

@ExperimentalContracts
@ExperimentalCoroutinesApi
@ExperimentalTime
internal class StatsCollectingSamplerImpl : Sampler() {

    private val probeTakingCollection = ProbeTakingStatisticsCollector()
    private val probeHandlingCollection = ProbeHandlingStatisticsCollector()

    override fun probe(onNewCoroutineFound: (ProfilingCoroutineInfo) -> Unit): CoroutinesDump {
        val dump: CoroutinesInfoDump

        probeTakingCollection.takeProbe {
            dump = dumpCoroutinesInfo() // Be careful: calling dumpCoroutinesInfo() updates probeID
        }

        val transformedDump: CoroutinesDump

        probeHandlingCollection.handleProbe {
            transformedDump = dump.transform(onNewCoroutineFound)
        }

        return transformedDump
    }

    override fun getHandlingStatistics(): ProbeHandlingStatistics = probeHandlingCollection.getStatistics()
    override fun getTakingStatistics(): ProbeTakingStatistics = probeTakingCollection.getStatistics()
}
