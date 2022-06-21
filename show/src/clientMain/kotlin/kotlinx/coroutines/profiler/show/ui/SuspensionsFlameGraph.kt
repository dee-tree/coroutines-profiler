package kotlinx.coroutines.profiler.show.ui

import api
import csstype.Display
import csstype.FlexDirection
import flamegraph.flamegraph
import flamegraph.select
import flamegraph.suspensionsColorMapper
import kotlinext.js.Object
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.show.serialization.CoroutineProbeFrame
import kotlinx.coroutines.profiler.show.serialization.CoroutineSuspensionsFrame
import kotlinx.coroutines.profiler.show.serialization.CoroutineSuspensionsFrame.Companion.asJsonValuedElement
import kotlinx.coroutines.profiler.show.serialization.CoroutineSuspensionsFrame.Companion.toCoroutineSuspensionsFrame
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToDynamic
import org.w3c.dom.HTMLDivElement
import react.*
import react.css.css
import react.dom.html.InputType
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label


private val scope = MainScope()

private val flameGraph = flamegraph()


private val SuspensionsFlameGraphProps.flameGraphTitle: String
    get() = "Suspensions ${
        when {
            coroutineId != null -> "coroutine #${coroutineId}"
            probeFrame != null -> "probe frame for coroutine #${probeFrame!!.coroutineId} at state ${probeFrame!!.coroutineState}"
            else -> ""
        }
    }"

val SuspensionsFlameGraph = FC<SuspensionsFlameGraphProps> { props ->

    val flameGraph = useMemo(callback = { flameGraph })

    val flameGraphContainer = useRef<HTMLDivElement>(null)

    var showSelectedCoroutineForEntireFlame by useState(false)


    useEffectOnce {
        flameGraph
            .setColorMapper(::suspensionsColorMapper)
            .setSearchMatch { d, term ->
                term.toLongOrNull()?.let {
                    d.asDynamic().data.coroutineValues[it]
                } ?: return@setSearchMatch false
            }
//        flameGraph.label(::coroutineFrameLabel)
    }

    useEffect {
        scope.launch {
            val rootFrame = props.probeFrame?.let {
                // suspensions only for selected probe
                it.toCoroutineSuspensionsFrame(api.getCoroutineReport(it.coroutineId))
            } ?: if (props.coroutineId == null || showSelectedCoroutineForEntireFlame) {
                // suspensions for all coroutines
                api.getSuspensionsStackTrace()
            } else {
                // suspensions for coroutine #selectedCoroutineId
                api.getSuspensionsStackTrace(props.coroutineId!!)
            }

            select("#${flameGraphContainer.current!!.id}").datum(Json.encodeToDynamic(rootFrame.asJsonValuedElement()) as Object)
                .call(flameGraph.title(props.flameGraphTitle))

            if (props.coroutineId != null && showSelectedCoroutineForEntireFlame) {
                search(props.coroutineId!!)
            }
        }

    }

    div {
        id = "suspensionsFlame"
        ref = flameGraphContainer
    }

    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.row
        }

        input {

            id = "show_suspensions_for_entire_flamegraph_checkbox"

            type = InputType.checkbox

            onChange = {
                showSelectedCoroutineForEntireFlame = it.currentTarget.checked
            }


        }

        label {
            htmlFor = "show_suspensions_for_entire_flamegraph_checkbox"
            +"Show selected coroutine on entire flamegraph"
        }
    }

}

fun search(coroutineId: Long) {
    flameGraph.search(coroutineId.toString())
}

external interface SuspensionsFlameGraphProps : Props {
    var coroutineId: Long?
    var probeFrame: CoroutineProbeFrame?
    var onFrameClicked: (CoroutineSuspensionsFrame) -> Unit
    var onExit: () -> Unit
}