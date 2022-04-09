package kotlinx.coroutines.profiler.sampling.internals

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.sampling.statistics.ProfilingInternalStatistics
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.system.measureTimeMillis

@ExperimentalCoroutinesApi
internal class StatsCollectingSamplerImpl : Sampler() {

    private var totalProbeTakingTime = 0
    private var maxProbeTakingTime = 0

    private var probesCount = 0
    private val probeTakingTimings = mutableMapOf<Int, Int>() // <probeTime to count of probes for this timing>


    override fun probe(onNewCoroutineFound: (ProfilingCoroutineInfo) -> Unit): ProfilingCoroutineDump {
        val dump: CoroutineInfoDump
        measureTimeMillis {
            dump = dumpCoroutinesInfo() // Be careful: calling dumpCoroutinesInfo() updates probeID
        }.also {
            val probeTime = it.toInt()
            totalProbeTakingTime += probeTime
            probesCount++
            probeTakingTimings[probeTime] = probeTakingTimings.getOrDefault(probeTime, 0) + 1
            if (it > maxProbeTakingTime) maxProbeTakingTime = probeTime
        }
        return dump.transform(onNewCoroutineFound)
    }

    @ExperimentalSerializationApi
    override fun getInternalStatistics(): ProfilingInternalStatistics.Builder {
        return ProfilingInternalStatistics.builder {
            meanProbeTakingTimeMillis = totalProbeTakingTime / probesCount
            maxProbeTakingTimeMillis = maxProbeTakingTime

            probeTakingQ1 = probeTakingTimings.percentile(25)
            probeTakingQ2 = probeTakingTimings.percentile(50)
            probeTakingQ3 = probeTakingTimings.percentile(75)

        }
    }
}

private fun Map<Int, Int>.percentile(p: Int): Int {
    require(p in 1..100)
    val elementsInTheRange = p * 0.01 * this.values.sum()

    var currentCount = 0

    keys.sorted().forEach { key ->
        if (this[key]!! + currentCount >= elementsInTheRange)
            return key

        currentCount += this[key]!!
    }

    throw IllegalStateException("unreachable code! Percentile must be returned earlier")
}