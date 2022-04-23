package kotlinx.coroutines.profiler.show.ui

import api
import flamegraph.*
import kotlinext.js.Object
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.show.serialization.CoroutineProbeFrame
import kotlinx.serialization.json.Json
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
                props.onFrameClicked(
                    CoroutineProbeFrame(
                        frame.data.name,
                        frame.data.value,
                        null,
                        frame.data.id,
                        frame.data.state,
                        frame.data.probesCount,
                        frame.data.stacktrace,
                        frame.data.thread
                    )
                )
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

//                props.selectedCoroutineId?.let {
//                    flameGraph.search(it.toString())
//                }
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
//    var coroutineSelection: Flow<Long?>
//    var selectedCoroutineId: Long?
    var onFrameClicked: (CoroutineProbeFrame) -> Unit
    var onExit: () -> Unit
}