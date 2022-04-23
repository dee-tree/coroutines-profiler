package kotlinx.coroutines.profiler.show.ui

import api
import csstype.*
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.core.data.ProfilingCoroutineInfo
import kotlinx.dom.addClass
import kotlinx.dom.hasClass
import react.FC
import react.Props
import react.css.css
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.p
import react.useEffectOnce
import react.useState

private val scope = MainScope()

val CoroutineReport = FC<CoroutineReportProps> { props ->
    kotlinext.js.require("./shadows.css")

    val thisElementId = "coroutineReport${props.coroutineId}"

    var coroutineInfo by useState(ProfilingCoroutineInfo(-1, "", null, emptyList()))

    useEffectOnce {
        scope.launch {
            coroutineInfo = api.getCoroutineReport(props.coroutineId)

        }
    }


    div {
        id = thisElementId

        onMouseEnter = {
            if (!document.getElementById(thisElementId)!!.hasClass("shadowedCoroutineReportBox"))
                document.getElementById(thisElementId)!!.addClass("shadowedCoroutineReportBox")
        }


        css {
            borderRadius = 0.5.em

            display = Display.flex
            flexDirection = FlexDirection.row
            justifyContent = JustifyContent.center
            alignItems = AlignItems.center

            0.5.em.let {
                paddingLeft = it
                paddingRight = it
            }
            margin = 0.5.em
        }


        onClick = {
            props.onCoroutineSelected(coroutineInfo)
            document.getElementById(thisElementId)!!.classList.add("selectedCoroutineReportBox")
        }

        tabIndex = 0

        p {
            +"${coroutineInfo.id} | ${coroutineInfo.name}"
        }
    }

}


external interface CoroutineReportProps : Props {
    var coroutineId: Long
    var onCoroutineSelected: (ProfilingCoroutineInfo) -> Unit
}