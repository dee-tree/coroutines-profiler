package kotlinx.coroutines.profiler.show.ui

import api
import csstype.*
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.core.data.ProfilingCoroutineInfo
import kotlinx.dom.removeClass
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.get
import react.*
import react.css.css
import react.dom.html.ReactHTML.div

private val scope = MainScope()

val CoroutinesReport = FC<CoroutinesReportProps> { props ->
    kotlinext.js.require("./shadows.css")

    var selectedCoroutineId: Long? by useState(null)
    val removeSelectionRef = useRef<HTMLDivElement>(null)

    var coroutinesIds by useState(emptyList<Long>())


    useEffectOnce {
        scope.launch {
            coroutinesIds = api.getAllCoroutinesIds()
        }
    }

    div {
        css {
            margin = 5.em
        }

        div {

            id = "shadowedCoroutinesReportBox"
            css {
                alignSelf = AlignSelf.flexStart

                borderRadius = 1.em
                borderStyle = LineStyle.solid
                borderColor = Color("gray")

                padding = 0.5.em
                borderWidth = 1.px

                maxHeight = 25.vh
                overflowY = OverflowY.auto
                scrollbarGutter = ScrollbarGutter.stable
            }


            for (coroutineId in coroutinesIds) {
                CoroutineReport() {
                    this.coroutineId = coroutineId
                    onCoroutineSelected = {
                        selectedCoroutineId?.let {
                            document.getElementsByClassName("selectedCoroutineReportBox")[0]!!.removeClass("selectedCoroutineReportBox")
                        }

                        selectedCoroutineId = it.id

                        props.onCoroutineSelected(it)

                        removeSelectionRef.current!!.removeAttribute("hidden")

                    }
                }
            }

        }


        div {
            ref = removeSelectionRef
            hidden = true

            css {
                padding = 1.em
            }
            +"Remove selection"

            onClick = {
                selectedCoroutineId = null

                removeSelectionRef.current!!.setAttribute("hidden", "true")
                document.getElementsByClassName("selectedCoroutineReportBox")[0]!!.removeClass("selectedCoroutineReportBox")
                props.onSelectionCleared()
            }

        }

    }
}

external interface CoroutinesReportProps : Props {
    var onCoroutineSelected: (ProfilingCoroutineInfo) -> Unit
    var onSelectionCleared: () -> Unit
}