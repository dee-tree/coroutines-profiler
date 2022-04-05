package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@kotlinx.serialization.Serializable
data class ProfilingResults(
    val structure: List<ProfilingCoroutineInfo>,
    val samples: List<ProfilingCoroutineSample>
)
