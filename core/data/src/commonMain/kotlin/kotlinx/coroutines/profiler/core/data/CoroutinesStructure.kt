package kotlinx.coroutines.profiler.core.data

import kotlin.jvm.JvmInline

/*

    A, B, C
  /  \    \
 D    E    F
 |
 G

 */
@JvmInline
value class CoroutinesStructure(
    val structure: List<StructuredProfilingCoroutineInfo>
)

/*

 A - B - C - D - E - F - G

 */
@JvmInline
value class LinearCoroutinesStructure(
    val coroutines: List<ProfilingCoroutineInfo>
)