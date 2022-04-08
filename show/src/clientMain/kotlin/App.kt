import flamegraph.flamegraph
import flamegraph.select
import kotlinext.js.Object
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.show.ProfilingInfo
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToDynamic
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.h4
import react.useEffectOnce
import react.useState

private val scope = MainScope()

val App = FC<Props> {
    var profilingInfo by useState(ProfilingInfo(0, 0, 0))

    val flamegraph = flamegraph()
        .title("Coroutines flame graph for dump")


    useEffectOnce {
        scope.launch {
            profilingInfo = getProfilingInfo()
        }

        scope.launch {
            select("#coroutinesFlame").datum(Json.encodeToDynamic(getStacks()) as Object).call(flamegraph)
        }
    }

    h3 {
        +"Coroutines profiler"
    }

    h4 {
        +"Coroutines: ${profilingInfo.coroutinesCount}\t\t"
        +"Samples: ${profilingInfo.samplesCount}\t\t"
        +"Time per sample: ${profilingInfo.samplesIntervalMillis} ms"
    }

    button {
        this.title = "Button"
    }
}