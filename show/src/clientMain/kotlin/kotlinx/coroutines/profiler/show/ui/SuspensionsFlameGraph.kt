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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToDynamic
import react.FC
import react.Props
import react.css.css
import react.dom.html.InputType
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.useEffectOnce


class SuspensionsFlameGraph {
    private val scope = MainScope()

    private val flameGraph = flamegraph()

    private var selectedCoroutineProbeFrame: CoroutineProbeFrame? = null
    private var selectedCoroutineId: Long? = null
    private var showSelectedCoroutineForEntireFlame = false

    val fc = FC<SuspensionsFlameGraphProps> { props ->
        flameGraph
            .title("Suspensions flame graph")
            .setColorMapper(::suspensionsColorMapper)
            .setSearchMatch { d, term ->
                term.toLongOrNull()?.let {
                    d.asDynamic().data.coroutineValues[it]
                } ?: return@setSearchMatch false
            }
//        flameGraph.label(::coroutineFrameLabel)

        useEffectOnce {
            scope.launch {
                showFlameGraph()
            }
        }

        div {
            id = "suspensionsFlame"
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

                    scope.launch {
                        showFlameGraph()
                    }
                }


            }

            label {
                htmlFor = "show_suspensions_for_entire_flamegraph_checkbox"
                +"Show selected coroutine on entire flamegraph"
            }

        }


    }

    fun showCoroutine(id: Long) {
        selectedCoroutineId = id

        scope.launch {
            showFlameGraph()
        }
    }

    fun showCoroutineProbeState(frame: CoroutineProbeFrame) {
        this.selectedCoroutineProbeFrame = frame

        scope.launch {
            showFlameGraph()
        }
    }

    fun search(coroutineId: Long) {
        flameGraph.search(coroutineId.toString())
    }

    fun clear() {
        selectedCoroutineId = null
        flameGraph.clear()
    }


    private suspend fun showFlameGraph() = scope.launch {
        val root = if (selectedCoroutineId == null || showSelectedCoroutineForEntireFlame) {
            api.getSuspensionsStackTrace()
        } else {
            if (selectedCoroutineProbeFrame != null) {
                val coroutineInfo = api.getCoroutineReport(selectedCoroutineProbeFrame!!.coroutineId)
                CoroutineSuspensionsFrame()
            }

            api.getSuspensionsStackTrace(selectedCoroutineId!!)
        }

        select("#suspensionsFlame").datum(Json.encodeToDynamic(root.asJsonValuedElement()) as Object)
            .call(flameGraph)

        if (selectedCoroutineId != null && showSelectedCoroutineForEntireFlame) {
            search(selectedCoroutineId!!)
        }

    }
}



external interface SuspensionsFlameGraphProps : Props {
    var onFrameClicked: (CoroutineSuspensionsFrame) -> Unit
    var onExit: () -> Unit
}