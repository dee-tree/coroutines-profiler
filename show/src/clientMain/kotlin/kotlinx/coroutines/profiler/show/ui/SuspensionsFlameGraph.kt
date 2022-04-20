package kotlinx.coroutines.profiler.show.ui

import api
import flamegraph.flamegraph
import flamegraph.select
import kotlinext.js.Object
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.show.serialization.CoroutineSuspensionsFrame
import kotlinx.coroutines.profiler.show.serialization.CoroutineSuspensionsFrame.Companion.asJsonValuedElement
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToDynamic
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffectOnce



class SuspensionsFlameGraph {
    private val scope = MainScope()

    private val flameGraph = flamegraph()

    val fc = FC<SuspensionsFlameGraphProps> { props ->
        flameGraph
            .title("Suspensions flame graph")
            .setColorHue("red")
//            .setSearchMatch { d, term ->
//                term.toLongOrNull()?.let {
//                    coroutineIdSearchMatch(d, it)
//                } ?: return@setSearchMatch false
//            }
//        flameGraph.label(::coroutineFrameLabel)

        useEffectOnce {
            scope.launch {
                val root = props.selectedCoroutineId?.let { api.getSuspensionsStackTrace(it) } ?: api.getSuspensionsStackTrace()
                select("#suspensionsFlame").datum(Json.encodeToDynamic(root.asJsonValuedElement()) as Object)
                    .call(flameGraph)
            }
        }

        div {
            id = "suspensionsFlame"
        }
    }

    fun search(coroutineId: Long) {
        flameGraph.search(coroutineId.toString())
    }

    fun clear() {
        flameGraph.clear()
    }

}

/*
private val scope = MainScope()
val SuspensionsFlameGraphx = FC<SuspensionsFlameGraphProps> { props ->

    val flamegraph = flamegraph()
        .title("Suspensions flame graph")
        .setColorHue("red")

    useEffectOnce {
        scope.launch {
            val root = api.getSuspensionsStackTrace()
            select("#suspensionsFlame").datum(Json.encodeToDynamic(root.asJsonValuedElement()) as Object)
                .call(flamegraph)
        }
    }


    div {
        id = "suspensionsFlame"
    }
}
*/


external interface SuspensionsFlameGraphProps : Props {
    var selectedCoroutineId: Long?
    var onFrameClicked: (CoroutineSuspensionsFrame) -> Unit
    var onExit: () -> Unit
}