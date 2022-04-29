package kotlinx.coroutines.profiler.show.ui

import api
import flamegraph.*
import kotlinext.js.Object
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.show.serialization.CoroutineProbeFrame
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic
import kotlinx.serialization.json.encodeToDynamic
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffectOnce

class CoroutinesFlameGraph {
    private val scope = MainScope()

    private val flameGraph = flamegraph()

    val fc = FC<CoroutinesFlameGraphProps> { props ->
        flameGraph
            .setColorMapper(::coroutineStateColorMapper)
            .title("Coroutines states sequence")
            .onClick { frame ->
                val frame = frame.asDynamic()
                if (frame.data.name == "root") {
                    props.onExit(); return@onClick
                }
                props.onFrameClicked(Json.decodeFromDynamic(frame.data))
            }
            .setSearchMatch { d, term ->
                term.toLongOrNull()?.let {
                    coroutineIdSearchMatch(d, it)
                } ?: return@setSearchMatch false
            }
        flameGraph.label(::coroutineFrameLabel)

        useEffectOnce {
            scope.launch {
                select("#flame").datum(Json.encodeToDynamic(api.getStacks()) as Object).call(flameGraph)
            }
        }


        div {
            id = "flame"
        }
    }

    fun search(coroutineId: Long) {
        flameGraph.search(coroutineId.toString())
    }

    fun clear() {
        flameGraph.clear()
    }
}

external interface CoroutinesFlameGraphProps : Props {
    var onFrameClicked: (CoroutineProbeFrame) -> Unit
    var onExit: () -> Unit
}