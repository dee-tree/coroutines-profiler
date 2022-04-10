@file:Suppress("EXPERIMENTAL_API_USAGE")
package kotlinx.coroutines.profiler.show.storage


import kotlinx.coroutines.profiler.sampling.data.CoroutinesProbes
import kotlinx.coroutines.profiler.sampling.data.CoroutinesStructure
import kotlinx.coroutines.profiler.sampling.statistics.ProfilingStatistics
import kotlinx.coroutines.profiler.show.serialization.ProfilingInfo
import java.io.File

private val UNINITIALIZED_PROFILING_STATS = ProfilingStatistics("", "", 0, 0, 0)

object ProfilingStorage {
    var coroutinesStructure: CoroutinesStructure = CoroutinesStructure(emptyList())
    var coroutinesProbes: CoroutinesProbes = CoroutinesProbes(emptyList())

    lateinit var profilingInfo: ProfilingInfo
    var profilingResults: ProfilingStatistics = UNINITIALIZED_PROFILING_STATS
    lateinit var profilingResultsFile: File

    fun isProfilingResultsInitialized(): Boolean = profilingResults != UNINITIALIZED_PROFILING_STATS

}

