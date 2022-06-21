package kotlinx.coroutines.profiler.show.ui

import api
import csstype.em
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.core.data.State
import kotlinx.coroutines.profiler.show.serialization.CoroutineProbeFrame
import react.FC
import react.Props
import react.css.css
import react.dom.html.ReactHTML.br
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h5
import react.dom.html.ReactHTML.hr
import react.useEffect
import react.useState


private val scope = MainScope()

val CoroutineInfo = FC<CoroutineInfoProps> { props ->

    val coroutineId = props.coroutineId ?: props.probeFrame?.coroutineId
    ?: error("Oooops... Coroutine id is unknown for CoroutineInfo")
    var coroutineName: String by useState("")
    var coroutineSuspensionRanges: Int by useState(0)
    var coroutineSuspensionFramesCount: Int by useState(-1)
    var coroutineRunningFramesCount: Int by useState(-1)
    var coroutineCreatedFramesCount: Int by useState(-1)

    var suspensionsCountAtSelectedProbeFrame: Int by useState(0)

    useEffect {
        scope.launch {
            val coroutineInfo = async {
                api.getCoroutineReport(coroutineId)
            }.await()
            coroutineName = props.probeFrame?.name ?: coroutineInfo.name


            launch {
                coroutineSuspensionRanges = api.getCoroutineStateRangesCount(coroutineId, State.SUSPENDED)
            }

            launch {
                coroutineSuspensionFramesCount = api.getCoroutineProbesCount(coroutineId, State.SUSPENDED)
            }

            launch {
                coroutineCreatedFramesCount = api.getCoroutineProbesCount(coroutineId, State.CREATED)
            }

            launch {
                coroutineRunningFramesCount = api.getCoroutineProbesCount(coroutineId, State.RUNNING)
            }

            props.probeFrame?.let { probeFrame ->
                if (probeFrame.coroutineState != State.SUSPENDED.toString())
                    return@let

                launch {
                    val probe = api.getCoroutineReport(probeFrame.coroutineId).probes.find {
                        it.coroutineId == probeFrame.coroutineId
                                && it.state == State.SUSPENDED
                                && it.lastUpdatedStackTrace == probeFrame.stacktrace
                    } ?: error("required probe not found!")
                    suspensionsCountAtSelectedProbeFrame =
                        api.getCoroutineSuspensionsCountWithSameStacktracesLikeProbeFrame(
                            probeFrame.coroutineId,
                            probe.probeId
                        )
                }
            }

        }
    }


    div {
        id = "coroutineProbeFrameBox"
        css {
            borderRadius = 2.em
            padding = 2.em
            margin = 2.em
        }

        h5 {
            +"${coroutineId} | ${coroutineName} ${props.probeFrame?.let { ": " + it.coroutineState } ?: ""}"
        }
        hr()

        props.probeFrame?.let { probeFrame ->
            +"Sampled at this frame ${probeFrame.probesCount} times"
            br()
            if (probeFrame.coroutineState == "RUNNING") +"At threads ${probeFrame.threads}"
            br()
            if (probeFrame.coroutineState == State.SUSPENDED.toString()) +"Suspended at selected frame ${suspensionsCountAtSelectedProbeFrame} times"


            hr()
        }

        +"Suspended ${coroutineSuspensionRanges} times"
        br()

        +"Sampled at CREATED state: $coroutineCreatedFramesCount times"
        br()
        +"Sampled at RUNNING state: $coroutineRunningFramesCount times"
        br()
        +"Sampled at SUSPENDED state: $coroutineSuspensionFramesCount times"
        br()


    }
}

external interface CoroutineInfoProps : Props {
    var probeFrame: CoroutineProbeFrame?
    var coroutineId: Long?
}