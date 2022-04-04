package kotlinx.coroutines.profiler.visual

import com.jakewharton.picnic.TextAlignment
import com.jakewharton.picnic.table
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo

@Suppress("EXPERIMENTAL_API_USAGE")
fun showCommonCoroutinesTable(coroutines: List<ProfilingCoroutineInfo>) {
    table {
        cellStyle {
            this.border = true
            this.alignment = TextAlignment.MiddleCenter
        }
        header {
            cellStyle { paddingLeft = 1; paddingRight = 1 }
            row {
                cell(null) {
                    columnSpan = 3
                }
                cell("Samples") {
                    columnSpan = 4
                }
            }
            row("coroutine id", "parent id", "kind", "total", "created", "running", "suspended")
        }

        body {
            coroutines.forEach { rootCoroutine ->
                rootCoroutine.walk {
                    row(
                        it.id,
                        it.parentId ?: "-",
//                        it.kind ?: "unknown",
                        it.totalExistenceSamples(),
                        it.totalCreatedSamples(),
                        it.totalRunningSamples(),
                        it.totalSuspendedSamples()
                    )
                }
            }
        }
    }.apply { println(this) }
}