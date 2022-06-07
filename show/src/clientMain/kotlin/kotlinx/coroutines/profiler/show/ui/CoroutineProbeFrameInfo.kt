package kotlinx.coroutines.profiler.show.ui

import csstype.em
import kotlinx.coroutines.profiler.show.serialization.CoroutineProbeFrame
import react.FC
import react.Props
import react.css.css
import react.dom.html.ReactHTML.br
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.h5

val CoroutineProbeFrameInfo = FC<CoroutineProbeFrameProps> { props ->


    div {
        id = "coroutineProbeFrameBox"
        css {
            borderRadius = 2.em
            padding = 2.em
            margin = 2.em
        }

        h5 {
            +"${props.probeFrame.coroutineId} | ${props.probeFrame.name} : ${props.probeFrame.coroutineState}"
        }

        +"Sampled at this state ${props.probeFrame.probesCount} times"
        br()
        if (props.probeFrame.coroutineState == "RUNNING") +"At threads: ${props.probeFrame.threads}"


    }
}

external interface CoroutineProbeFrameProps : Props {
    var probeFrame: CoroutineProbeFrame
}