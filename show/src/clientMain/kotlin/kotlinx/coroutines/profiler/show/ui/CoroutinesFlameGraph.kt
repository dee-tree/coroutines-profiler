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
import react.*
import react.dom.html.InputType
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label

private val scope = MainScope()


private val flameGraph = flamegraph()

val CoroutinesFlameGraph = FC<CoroutinesFlameGraphProps> { props ->

    val flameGraph = useMemo<FlameGraph>(callback = { flameGraph })

    var canBeCleared: Boolean by useState(false)

    useEffectOnce {
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
            .label(::coroutineFrameLabel)

        scope.launch {
            select("#flame").datum(Json.encodeToDynamic(api.getStacks()) as Object).call(flameGraph)
        }
    }

    useEffect {
        props.coroutineIdToSearch?.let {
            flameGraph.search(it.toString())
            canBeCleared = true
        } ?: run {
            if (canBeCleared) {
                flameGraph.clear()
                canBeCleared = false
            }
        }
    }


    div {
        id = "flame"
    }


    div {
        input {
            type = InputType.checkbox
            id = "cb_combine_same_states"

        }
        label {
            htmlFor = "cb_combine_same_states"
            +"Combine same states on timeline"
        }
    }
}


external interface CoroutinesFlameGraphProps : Props {
    var coroutineIdToSearch: Long?
    var onFrameClicked: (CoroutineProbeFrame) -> Unit
    var onExit: () -> Unit
}