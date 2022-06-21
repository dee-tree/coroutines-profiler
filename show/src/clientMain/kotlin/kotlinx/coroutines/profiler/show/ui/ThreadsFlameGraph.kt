package kotlinx.coroutines.profiler.show.ui

import api
import flamegraph.flamegraph
import flamegraph.select
import kotlinext.js.Object
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.show.serialization.CoroutineSuspensionsFrame
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToDynamic
import org.w3c.dom.HTMLDivElement
import react.*
import react.dom.html.ReactHTML.div

private val scope = MainScope()

private val flamegraph = flamegraph()


val ThreadsFlameGraph = FC<ThreadsFlameGraphProps> { props ->

    val flameGraph = useMemo(callback = { flamegraph })

    val flameGraphContainerRef = useRef<HTMLDivElement>(null)

    useEffectOnce {
        flameGraph
            .title("Threads")
    }

    useEffect {
        scope.launch {
            val root = api.getThreadsFrame(props.coroutineId)

            select("#${flameGraphContainerRef.current!!.id}").datum(Json.encodeToDynamic(root) as Object)
                .call(flameGraph)
        }
    }


    div {
        id = "threadsFlameGraphContainer"
        ref = flameGraphContainerRef
    }
}


external interface ThreadsFlameGraphProps : Props {
    var coroutineId: Long
    var onFrameClicked: (CoroutineSuspensionsFrame) -> Unit
    var onExit: () -> Unit
}