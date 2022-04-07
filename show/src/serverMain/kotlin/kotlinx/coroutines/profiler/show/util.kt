package kotlinx.coroutines.profiler.show

import kotlinx.coroutines.profiler.sampling.ProfilingResultsFile

fun ProfilingResultsFile.extractInfo(): ProfilingInfo = ProfilingInfo(
    coroutinesCount,
    samplesCount,
    samplesIntervalMillis
)