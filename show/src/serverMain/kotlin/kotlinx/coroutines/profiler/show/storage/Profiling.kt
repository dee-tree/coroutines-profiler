@file:Suppress("EXPERIMENTAL_API_USAGE")
package kotlinx.coroutines.profiler.show.storage


import kotlinx.coroutines.profiler.sampling.data.CoroutinesProbes
import kotlinx.coroutines.profiler.sampling.data.CoroutinesStructure
import kotlinx.coroutines.profiler.sampling.statistics.ProfilingStatistics
import kotlinx.coroutines.profiler.show.serialization.ProfilingInfo
import java.io.File


var coroutinesStructure: CoroutinesStructure = CoroutinesStructure(emptyList())
var coroutinesProbes: CoroutinesProbes = CoroutinesProbes(emptyList())
lateinit var profilingInfo: ProfilingInfo
lateinit var profilingResults: ProfilingStatistics
lateinit var profilingResultsFile: File