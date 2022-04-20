package kotlinx.coroutines.profiler.show.ui

import api
import csstype.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.core.data.ProfilingCoroutineInfo
import react.FC
import react.Props
import react.css.css
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.p
import react.useEffectOnce
import react.useState

private val scope = MainScope()

val CoroutineReport = FC<CoroutineReportProps> { props ->
    var coroutineInfo by useState(ProfilingCoroutineInfo(-1, "", null, emptyList()))

    useEffectOnce {
        scope.launch {
            coroutineInfo = api.getCoroutineReport(props.coroutineId)

        }
    }

    div {
        css {
            alignSelf = AlignSelf.flexStart

            focus {
                backgroundColor = Color("#9C96A8")
            }

            borderRadius = 0.5.em
            borderStyle = LineStyle.solid
            borderColor = Color("gray")

            paddingLeft = 1.em
            paddingRight = 2.em
            paddingBottom = 0.25.em
            paddingTop = 0.25.em
            margin = 0.5.em
            borderWidth = 1.px
        }

        onFocus = {
            props.onFocus(coroutineInfo)
        }

        tabIndex = 0

        p {
            +"Coroutine ${coroutineInfo.name}\twith id = ${coroutineInfo.id}"
        }
    }

}


external interface CoroutineReportProps : Props {
    var coroutineId: Long
    var onFocus: (ProfilingCoroutineInfo) -> Unit
}