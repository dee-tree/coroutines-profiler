package kotlinx.coroutines.profiler.show

import kotlinx.coroutines.profiler.sampling.statistics.ProfilingStatistics
import kotlinx.coroutines.profiler.show.serialization.ProfilingInfo

@Suppress("EXPERIMENTAL_API_USAGE")
fun ProfilingStatistics.extractInfo(): ProfilingInfo = ProfilingInfo(
    coroutinesCount,
    probesCount,
    specifiedProbesIntervalMillis
)