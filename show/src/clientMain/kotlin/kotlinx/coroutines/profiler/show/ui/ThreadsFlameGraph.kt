package kotlinx.coroutines.profiler.show.ui

import flamegraph.flamegraph
import flamegraph.select
import kotlinext.js.Object
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.show.serialization.CoroutineProbeFrame
import kotlinx.coroutines.profiler.show.serialization.CoroutineSuspensionsFrame
import kotlinx.coroutines.profiler.show.serialization.CoroutineSuspensionsFrame.Companion.asJsonValuedElement
import kotlinx.coroutines.profiler.show.serialization.CoroutineSuspensionsFrame.Companion.toCoroutineSuspensionsFrame
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToDynamic
import react.FC
import react.Props

class ThreadsFlameGraph {
    private val scope = MainScope()

    private val flamegraph = flamegraph()

    private var selectedCoroutineId: Long? =  null
    private var selectedCoroutineProbeFrame: CoroutineProbeFrame? = null


    val fc = FC<ThreadsFlameGraphProps> { props ->
        flamegraph
            .title("Threads")

    }

    /*private suspend fun showFlameGraph() = scope.launch {
        val root = selectedCoroutineProbeFrame?.let {
//            suspensions only for selected probe
            it.toCoroutineSuspensionsFrame(api.getCoroutineReport(it.coroutineId))
        } ?: if (selectedCoroutineId == null || showSelectedCoroutineForEntireFlame) {
//            suspensions for all coroutines
            api.getSuspensionsStackTrace()
        } else {
//            suspensions for coroutine #selectedCoroutineId
            api.getSuspensionsStackTrace(selectedCoroutineId!!)
        }

        select("#suspensionsFlame").datum(Json.encodeToDynamic(root.asJsonValuedElement()) as Object)
            .call(flameGraph)

        if (selectedCoroutineId != null && showSelectedCoroutineForEntireFlame) {
            search(selectedCoroutineId!!)
        }

    }*/

}

external interface ThreadsFlameGraphProps : Props {
    var onFrameClicked: (CoroutineSuspensionsFrame) -> Unit
    var onExit: () -> Unit
}