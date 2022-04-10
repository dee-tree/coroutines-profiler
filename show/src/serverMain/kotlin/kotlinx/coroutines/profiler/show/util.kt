package kotlinx.coroutines.profiler.show

import kotlinx.coroutines.profiler.sampling.statistics.ProfilingStatistics
import kotlinx.coroutines.profiler.show.serialization.ProfilingInfo
import kotlinx.coroutines.profiler.show.storage.ProfilingStorage
import java.io.File

@Suppress("EXPERIMENTAL_API_USAGE")
fun ProfilingStatistics.extractInfo(): ProfilingInfo = ProfilingInfo(
    coroutinesCount,
    probesCount,
    specifiedProbesIntervalMillis
)

@Suppress("EXPERIMENTAL_API_USAGE")
fun loadProfilingResults(fromFile: File) {
    ProfilingStorage.profilingResults = ProfilingStatistics.fromFile(fromFile)
}