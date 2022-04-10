import flamegraph.flamegraph
import flamegraph.select
import kotlinext.js.Object
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.show.serialization.ProfilingInfo
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToDynamic
import react.FC
import react.Props
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

    val flamegraph = flamegraph()
        .title("Coroutines flame graph for dump")


    useEffectOnce {
        scope.launch {
            profilingInfo = api.getProfilingInfo()

            select("#flame").datum(Json.encodeToDynamic(api.getStacks()) as Object).call(flamegraph)
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

//        css {
//            paddingTop = Padding("10px")
//            display = Display.flex
//            justifyContent = JustifyContent.center
//        }

        nav {
            div {
                className = "pull-right"
                form {
                    className = "form-inline"
                    id = "form"

                    a {
                        className = "btn"
                        + "Reset zoom"
                    }
                }
            }
        }
    }

    div {
        id = "content"


        h3 {
            + "Coroutines profiler"
        }

        h4 {
            + "Coroutines: ${profilingInfo.coroutinesCount}\t\t"
            + "Samples: ${profilingInfo.samplesCount}\t\t"
            + "Time per sample: ${profilingInfo.probesIntervalMillis} ms"
        }

        div {
            id = "flame"
        }
    }


}