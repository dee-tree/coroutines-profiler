package kotlinx.coroutines.profiler.show.ui

import api
import csstype.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.core.data.ProfilingCoroutineInfo
import kotlinx.dom.addClass
import kotlinx.dom.hasClass
import org.w3c.dom.HTMLDivElement
import react.*
import react.css.css
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.p

private val scope = MainScope()

val CoroutineReport = FC<CoroutineReportProps> { props ->
    kotlinext.js.require("./shadows.css")

    val containerRef = useRef<HTMLDivElement>(null)

    var coroutineInfo by useState(ProfilingCoroutineInfo(-1, "", null, emptyList()))

    useEffectOnce {
        scope.launch {
            coroutineInfo = api.getCoroutineReport(props.coroutineId)

        }
    }


    div {
        ref = containerRef

        onMouseEnter = {
            if (!containerRef.current!!.hasClass("shadowedCoroutineReportBox"))
                containerRef.current!!.addClass("shadowedCoroutineReportBox")
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
            containerRef.current!!.classList.add("selectedCoroutineReportBox")
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