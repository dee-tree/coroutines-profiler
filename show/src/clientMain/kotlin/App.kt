import csstype.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.core.data.statistics.ProfilingStatistics
import kotlinx.coroutines.profiler.show.ui.CoroutinesFlameGraph
import react.FC
import react.Props
import react.css.css
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.details
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.h5
import react.dom.html.ReactHTML.nav
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.summary
import react.useEffectOnce
import react.useState

private val scope = MainScope()

val App = FC<Props> {
    var profilingInfo by useState(ProfilingStatistics(0, 0, 0))

    useEffectOnce {
        scope.launch {
            profilingInfo = api.getProfilingStatistics()
        }
    }

    /*
    <div class="header clearfix" id="navigation">
        <nav>
            <div class="pull-right">
                <form class="form-inline" id="form">
                    <a class="btn" href="javascript: resetZoom();">Reset zoom</a>
                    <a class="btn" href="javascript: clear();">Clear</a>
                    <a class="btn" href="javascript: clear();">Clear another</a>
                    <div class="form-group">
                        <input type="text" class="form-control" id="term"/>
                    </div>
                    <a class="btn btn-primary" href="javascript: search();">Search</a>
                </form>
            </div>
        </nav>
    </div>

     */

    div {
        className = "header clearfix"
        id = "navigation"

        css {
            paddingTop = 10.px
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
        }

        nav {
            div {
                className = "pull-right"
                form {
                    className = "form-inline"
                    id = "form"

                    a {
                        className = "btn"
                        +"Reset zoom"
                    }
                }
            }
        }
    }

    div {
        id = "content"

        css {
            paddingTop = 1.vh
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
        }


        h3 {
            +"Coroutines profiler"
        }

        h4 {
            +"Statistics"
        }
        p {
            +"Coroutines: ${profilingInfo.coroutinesCount}\t\t"
            +"Probes: ${profilingInfo.probesCount}\t\t"
            +"Specified probes interval: ${profilingInfo.specifiedProbesIntervalMillis} ms"
        }

        profilingInfo.internalStatistics?.let { internal ->

            details {
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

        CoroutinesFlameGraph() {
            onFrameClicked = {
                scope.launch {
                    api.coroutine(it.coroutineId)
                }

            }
        }
    }


}