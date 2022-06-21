package kotlinx.coroutines.profiler.show.ui

import api
import csstype.*
import jetbrains.letsPlot.Pos
import jetbrains.letsPlot.frontend.JsFrontendUtil
import jetbrains.letsPlot.geom.geomHistogram
import jetbrains.letsPlot.letsPlot
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.core.data.statistics.ProfilingStatistics
import org.w3c.dom.HTMLDivElement
import react.*
import react.css.css
import react.dom.html.ReactHTML.b
import react.dom.html.ReactHTML.br
import react.dom.html.ReactHTML.details
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.h5
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.summary

private val scope = MainScope()
const val takingDivId: String = "probeTakingDiv"
const val handlingDivId: String = "probeHandlingDiv"

val StatisticsComponent = FC<StatsProps> {

    var histParentRef = useRef<HTMLDivElement>(null)

    var profilingStatistics by useState(ProfilingStatistics(0, 0, 0))

    useEffectOnce {
        scope.launch {
            profilingStatistics = api.getProfilingStatistics()
        }
    }

    div {

        css {
            display = Display.flex
            flexDirection = FlexDirection.row
            justifyContent = JustifyContent.center

            alignSelf = AlignSelf.center
        }

        div {
            css {
                flexDirection = FlexDirection.column
                padding = 5.em
            }

            h4 {
                +"Statistics"
            }

            p {
                +"Coroutines: ${profilingStatistics.coroutinesCount}"
                br()
                +"Probes: ${profilingStatistics.probesCount}"
                br()
                +"Specified probes interval: ${profilingStatistics.specifiedProbesIntervalMillis} ms"
            }

        }

        profilingStatistics.internalStatistics?.let { internal ->

            details {
                css {
                    padding = 5.em
                }

                summary {
                    css {

                        hover {
                            color = Color("#ff3200")

                        }
                    }
                    h4 {
                        +"Internal statistics"
                    }
                }


                div {
                    css {
                        display = Display.flex
                        flexDirection = FlexDirection.row

                    }


                    div {
                        id = takingDivId

                        css {
                            paddingRight = 1.em
                            paddingLeft = 1.em
                        }

                        h5 {
                            b {
                                +"Probe taking"
                            }
                        }
                        p {
                            +"Mean time: ${internal.probeTakingStatistics.meanTimeMillis} ms"
                            br()
                            +"Max time: ${internal.probeTakingStatistics.maxTimeMillis} ms"
                            br()
                            +"Time Q1: ${internal.probeTakingStatistics.probeTakingQ1} ms"
                            br()
                            +"Time Q2: ${internal.probeTakingStatistics.probeTakingQ2} ms"
                            br()
                            +"Time Q3: ${internal.probeTakingStatistics.probeTakingQ3} ms"
                        }
                    }


                    div {
                        id = handlingDivId
                        css {
                            paddingRight = 1.em
                            paddingLeft = 1.em
                        }
                        h5 {
                            b {
                                +"Probe handling"
                            }
                        }
                        p {
                            +"Mean time: ${internal.probeHandlingStatistics.meanProbeHandlingTimeMillis} ms"
                            br()
                            +"Max time: ${internal.probeHandlingStatistics.maxProbeHandlingTimeMillis} ms"
                            br()
                            +"Time Q1: ${internal.probeHandlingStatistics.probeHandlingQ1} ms"
                            br()
                            +"Time Q2: ${internal.probeHandlingStatistics.probeHandlingQ2} ms"
                            br()
                            +"Time Q3: ${internal.probeHandlingStatistics.probeHandlingQ3} ms"
                        }
                    }


                }

            }

        }

    }

    div {
        ref = histParentRef
    }

    profilingStatistics.internalStatistics?.let { internal ->
        val takingSampling = internal.probeTakingStatistics.takingTimingsSampling.toMutableMap()
        val handlingSampling = internal.probeHandlingStatistics.handlingTimingsSampling.toMutableMap()

        val dataTaking =
            takingSampling.entries.fold(emptyList<Long>()) { acc, cur -> acc + List(cur.value) { cur.key } }
        val dataHandling =
            handlingSampling.entries.fold(emptyList<Long>()) { acc, cur -> acc + List(cur.value) { cur.key } }
        val hist = letsPlot(
            mapOf(
                "timings" to dataTaking + dataHandling,
                "cond" to List(dataTaking.size) { "take probes" } + List(dataHandling.size) { "handle probes" })
        ) { x = "timings"; fill = "cond" } + geomHistogram(
            alpha = 0.7,
            position = Pos.identity
        )

        while (histParentRef.current!!.lastChild != null) {
            histParentRef.current!!.removeChild(histParentRef.current!!.lastChild!!)
        }
        histParentRef.current!!.appendChild(JsFrontendUtil.createPlotDiv(hist))
    }
}

external interface StatsProps : Props {
}