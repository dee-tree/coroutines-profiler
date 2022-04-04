@file:Suppress("EXPERIMENTAL_API_USAGE")

package kotlinx.coroutines.profiler.visual

import com.jakewharton.picnic.TextAlignment
import com.jakewharton.picnic.table
import kotlinx.coroutines.debug.State
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.visual.CoroutineStatesRange.Companion.splitByStates

internal fun showCoroutineTable(coroutine: ProfilingCoroutineInfo) {
    println("Coroutine ${coroutine.name}: #${coroutine.id}")

    showCoroutineSamplesGrouping(coroutine)
    showCoroutineThreadsGrouping(coroutine)
    showCoroutineStatesRanges(coroutine)

    println("Children:")
    println(coroutine)

    println("creation: \n${coroutine.creationStackTrace.joinToString("\n") { "\t at $it" }}")

}

internal fun showCoroutineStatesRanges(coroutine: ProfilingCoroutineInfo) {
    table {
        cellStyle {
            this.padding = 0
            border = true
            this.alignment = TextAlignment.MiddleCenter
        }
        coroutine.walk {
            row {
                cell("Coroutine #${it.id}")
                cell("-") {
                    columnSpan = it.samples.firstOrNull()?.dumpId?.toInt() ?: 0
                }

                it.splitByStates().forEach { state ->
                    val stateInfo = "${state.fromSample} - ${state.toSample}: ${state.state} ${ when (state.state) {
                        State.RUNNING -> (state.lastStackTrace.lastOrNull()?.let { "@$it " } ?: "") + "on ${state.thread}"
                        State.SUSPENDED -> (state.lastStackTrace.lastOrNull()?.let { "@$it " } ?: "")
                        State.CREATED -> ""
                    }}"
                    cell(stateInfo) {
                        columnSpan = (state.toSample - state.fromSample).toInt() + 1
                    }
                }
            }


        }

    }.apply { println(this) }
}

internal fun showCoroutineThreadsGrouping(coroutine: ProfilingCoroutineInfo) {
    table {
        cellStyle {
            this.border = true
            this.alignment = TextAlignment.MiddleCenter
        }

        header {
            row {
                cell("Threads pool") {
                    columnSpan = 2
                }
            }
            row("thread", "samples")
        }
        body {
            coroutine.threads().forEach { thread, samples ->
                row(thread, "$samples (${samples * 100 / coroutine.totalRunningSamples()}%)")
            }
        }

    }.apply { println(this) }
}


internal fun showCoroutineSamplesGrouping(coroutine: ProfilingCoroutineInfo) {
    table {
        cellStyle {
            this.border = true
            this.alignment = TextAlignment.MiddleCenter
        }

        header {
            row {
                cell("Samples") {
                    columnSpan = 4
                }
            }

            row("total", "created", "running", "suspended")
        }

        body {
            row(
                "${coroutine.totalExistenceSamples()}",
                "${coroutine.totalCreatedSamples()} (${coroutine.totalCreatedSamplesComparative()})",
                "${coroutine.totalRunningSamples()} (${coroutine.totalRunningSamplesComparative()})",
                "${coroutine.totalSuspendedSamples()} (${coroutine.totalSuspendedSamplesComparative()})",
            )
        }

    }.apply { println(this) }
}