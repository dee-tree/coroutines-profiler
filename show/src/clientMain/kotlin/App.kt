import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.show.ProfilingInfo
import react.FC
import react.Props
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.h4
import react.useEffectOnce
import react.useState

private val scope = MainScope()

val App = FC<Props> {
    var profilingInfo by useState(ProfilingInfo(0, 0, 0))
    useEffectOnce {
        scope.launch {
            profilingInfo = getProfilingInfo()
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
}