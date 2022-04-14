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
) {
    fun find(condition: (StructuredProfilingCoroutineInfo) -> Boolean): StructuredProfilingCoroutineInfo? {
        structure.forEach { info ->
            info.find(condition)?.let { return it }
        }
        return null
    }

    fun walk(action: (StructuredProfilingCoroutineInfo) -> Unit) {
        structure.forEach { info ->
            info.walk(action)
        }
    }

}

/*

 A - B - C - D - E - F - G

 */
@JvmInline
value class LinearCoroutinesStructure(
    val coroutines: List<ProfilingCoroutineInfo>
)