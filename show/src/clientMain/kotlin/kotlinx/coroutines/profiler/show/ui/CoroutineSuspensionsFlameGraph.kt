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

private val scope = MainScope()
val CoroutineSuspensionsFlameGraph = FC<CoroutineSuspensionsFlameGraphProps> { props ->

    val flamegraph = flamegraph()
        .title("Coroutine #${props.coroutineId} suspensions")

    useEffectOnce {
        scope.launch {
            val root = api.getSuspensionsStackTrace(props.coroutineId)
            select("#coroSuspensionsFlame").datum(Json.encodeToDynamic(root.asJsonValuedElement()) as Object)
                .call(flamegraph)
        }
    }


    div {
        id = "coroSuspensionsFlame"
    }
}

external interface CoroutineSuspensionsFlameGraphProps : Props {
    var coroutineId: Long
    var onFrameClicked: (CoroutineSuspensionsFrame) -> Unit
    var onExit: () -> Unit
}