package kotlinx.coroutines.profiler.core.data

import kotlin.jvm.JvmInline

@JvmInline
value class Probes(
    val probes: List<CoroutineProbe>
)