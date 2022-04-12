package kotlinx.coroutines.profiler.core.internals.statistics

import kotlinx.coroutines.profiler.core.data.statistics.ProbeTakingStatistics
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
internal class ProbeTakingStatisticsCollector {
    private var totalProbeTakingTime = 0L
    private var maxProbeTakingTime = 0L

    private var probesCount = 0
    private val probeTakingTimings = mutableMapOf<Long, Int>() // <probeTime to count of probes for this timing>


    @ExperimentalContracts
    @ExperimentalTime
    inline fun takeProbe(crossinline block: () -> Unit) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }

        val probeTime = measureTime(block).inWholeMilliseconds

        totalProbeTakingTime += probeTime
        probesCount++
        probeTakingTimings[probeTime] = probeTakingTimings.getOrElse(probeTime) { 0 } + 1
        if (probeTime > maxProbeTakingTime) maxProbeTakingTime = probeTime

    }

    fun getStatistics(): ProbeTakingStatistics = ProbeTakingStatistics(
        totalProbeTakingTime / probesCount,
        maxProbeTakingTime,
        probeTakingTimings.percentile(25),
        probeTakingTimings.percentile(50),
        probeTakingTimings.percentile(75),
    )
}

internal fun Map<Long, Int>.percentile(p: Int): Long {
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