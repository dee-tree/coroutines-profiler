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
import react.useEffectOnce
import react.useState

private val scope = MainScope()

val CoroutinesReport = FC<CoroutinesReportProps> { props ->
    var coroutinesIds by useState(emptyList<Long>())


    useEffectOnce {
        scope.launch {
            coroutinesIds = api.getAllCoroutinesIds()
        }
    }

    div {
        css {
            alignSelf = AlignSelf.flexStart

            borderRadius = 1.em
            borderStyle = LineStyle.solid
            borderColor = Color("gray")

            padding = 0.5.em
            margin = 0.5.em
            borderWidth = 1.px
        }

        onBlur = {
            props.onCoroutineLoseFocus()
        }

        for (coroutineId in coroutinesIds) {
            CoroutineReport() {
                this.coroutineId = coroutineId
                onFocus = {
                    println("Focused coro ${it}")
                    props.onCoroutineFocus(it)
                }
            }
        }

    }
}

external interface CoroutinesReportProps : Props {
    var onCoroutineFocus: (ProfilingCoroutineInfo) -> Unit
    var onCoroutineLoseFocus: () -> Unit
}