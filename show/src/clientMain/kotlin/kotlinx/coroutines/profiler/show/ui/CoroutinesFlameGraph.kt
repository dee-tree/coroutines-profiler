package kotlinx.coroutines.profiler.show.ui

import api
import flamegraph.coroutineFrameLabel
import flamegraph.coroutineStateColorMapper
import flamegraph.flamegraph
import flamegraph.select
import kotlinext.js.Object
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.show.serialization.CoroutineProbeFrame
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToDynamic
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffectOnce

private val scope = MainScope()
val CoroutinesFlameGraph = FC<CoroutinesFlameGraphProps> { props ->

    val flamegraph = flamegraph()
        .setColorMapper(::coroutineStateColorMapper)
        .title("Coroutines flame graph for dump")
        .onClick { frame ->
            val frame = frame.asDynamic()
            if (frame.data.name == "root") { props.onExit(); return@onClick }
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

    flamegraph.label(::coroutineFrameLabel)

    useEffectOnce {
        scope.launch {
            delay(100)
            select("#flame").datum(Json.encodeToDynamic(api.getStacks()) as Object).call(flamegraph)
        }
    }


    div {
        id = "flame"
    }
}

external interface CoroutinesFlameGraphProps : Props {

    var onFrameClicked: (CoroutineProbeFrame) -> Unit
    var onExit: () -> Unit
}