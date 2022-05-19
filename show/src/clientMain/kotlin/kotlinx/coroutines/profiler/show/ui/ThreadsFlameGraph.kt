package kotlinx.coroutines.profiler.show.ui

import api
import flamegraph.flamegraph
import flamegraph.select
import kotlinext.js.Object
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.show.serialization.CoroutineSuspensionsFrame
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToDynamic
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffectOnce

class ThreadsFlameGraph {
    private val scope = MainScope()

    private val flamegraph = flamegraph()

    private var selectedCoroutineId: Long? = null

    private val flameGraphContainerId = "threadsFlame"


    val fc = FC<ThreadsFlameGraphProps> { props ->
        flamegraph
            .title("Threads")


        useEffectOnce {
            scope.launch {
                showFlameGraph()
            }
        }

        div {
            id = flameGraphContainerId
        }

    }


    fun clear() {
        selectedCoroutineId = null
        flamegraph.clear()
    }

    fun showCoroutine(id: Long) {
        selectedCoroutineId = id

        scope.launch {
            println("Show flame graph")
            showFlameGraph()
        }
    }
    private suspend fun showFlameGraph() = scope.launch {
        val root = selectedCoroutineId?.let {

            api.getThreadsFrame(it)
        } ?: return@launch

        select("#$flameGraphContainerId").datum(Json.encodeToDynamic(root) as Object)
            .call(flamegraph)
    }

}

external interface ThreadsFlameGraphProps : Props {
    var onFrameClicked: (CoroutineSuspensionsFrame) -> Unit
    var onExit: () -> Unit
}