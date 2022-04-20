package kotlinx.coroutines.profiler.show.ui

import api
import csstype.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.core.data.statistics.ProfilingStatistics
import react.FC
import react.Props
import react.css.css
import react.dom.html.ReactHTML.details
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.h5
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.summary
import react.useEffectOnce
import react.useState

private val scope = MainScope()

val StatisticsComponent = FC<Props> {
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
                +"Coroutines: ${profilingStatistics.coroutinesCount}\t\t"
                +"Probes: ${profilingStatistics.probesCount}\t\t"
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
                        display = Display.flex
                        alignItems = AlignItems.center
                        justifyContent = JustifyContent.center

                        hover {
                            color = Color("#ff3200")

                        }
                    }
                    h4 {
                        +"Internal statistics"
                    }
                }
                h5 {
                    +"Probe taking"
                }
                p {
                    +"Mean time: ${internal.probeTakingStatistics.meanTimeMillis} ms\t\t"
                    +"Max time: ${internal.probeTakingStatistics.maxTimeMillis} ms\t\t"
                    +"Time Q1: ${internal.probeTakingStatistics.probeTakingQ1} ms\t\t"
                    +"Time Q2: ${internal.probeTakingStatistics.probeTakingQ2} ms\t\t"
                    +"Time Q3: ${internal.probeTakingStatistics.probeTakingQ3} ms\t\t"
                }
                h5 {
                    +"Probe handling"
                }
                p {
                    +"Mean time: ${internal.probeHandlingStatistics.meanProbeHandlingTimeMillis} ms\t\t"
                    +"Max time: ${internal.probeHandlingStatistics.maxProbeHandlingTimeMillis} ms\t\t"
                    +"Time Q1: ${internal.probeHandlingStatistics.probeHandlingQ1} ms\t\t"
                    +"Time Q2: ${internal.probeHandlingStatistics.probeHandlingQ2} ms\t\t"
                    +"Time Q3: ${internal.probeHandlingStatistics.probeHandlingQ3} ms\t\t"
                }
            }

        }

    }

}