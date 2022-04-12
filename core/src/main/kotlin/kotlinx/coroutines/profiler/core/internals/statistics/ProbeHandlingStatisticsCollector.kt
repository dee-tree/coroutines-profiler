package kotlinx.coroutines.profiler.core.internals.statistics

import kotlinx.coroutines.profiler.core.data.statistics.ProbeHandlingStatistics
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
internal class ProbeHandlingStatisticsCollector {
    private var totalProbeHandlingTime = 0L
    private var maxProbeHandlingTime = 0L

    private var timesHandled = 0
    private val probeHandlingTimings = mutableMapOf<Long, Int>() // <probeTime to count of probes for this timing>

    @ExperimentalContracts
    inline fun handleProbe(block: () -> Unit) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }

        val handlingTime = measureTime(block).inWholeMilliseconds

        totalProbeHandlingTime += handlingTime
        timesHandled++
        probeHandlingTimings[handlingTime] = probeHandlingTimings.getOrElse(handlingTime) { 0 } + 1
        if (handlingTime > maxProbeHandlingTime) maxProbeHandlingTime = handlingTime

    }


    fun getStatistics(): ProbeHandlingStatistics = ProbeHandlingStatistics(
        totalProbeHandlingTime / timesHandled,
        maxProbeHandlingTime,
        probeHandlingTimings.percentile(25),
        probeHandlingTimings.percentile(50),
        probeHandlingTimings.percentile(75),
    )
}
