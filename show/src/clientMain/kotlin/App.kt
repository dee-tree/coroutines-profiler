import csstype.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.show.serialization.ProfilingInfo
import kotlinx.coroutines.profiler.show.ui.CoroutinesFlameGraph
import react.FC
import react.Props
import react.css.css
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.nav
import react.useEffectOnce
import react.useState

private val scope = MainScope()

val App = FC<Props> {
    var profilingInfo by useState(ProfilingInfo(0, 0, 0))

    useEffectOnce {
        scope.launch {
            profilingInfo = api.getProfilingInfo()
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
            +"Coroutines: ${profilingInfo.coroutinesCount}\t\t"
            +"Samples: ${profilingInfo.samplesCount}\t\t"
            +"Time per sample: ${profilingInfo.probesIntervalMillis} ms"
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