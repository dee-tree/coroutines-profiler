import csstype.AlignItems
import csstype.Display
import csstype.FlexDirection
import csstype.vh
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.show.ui.CoroutinesFlameGraph
import kotlinx.coroutines.profiler.show.ui.CoroutinesReport
import kotlinx.coroutines.profiler.show.ui.StatisticsComponent
import kotlinx.coroutines.profiler.show.ui.SuspensionsFlameGraph
import react.FC
import react.Props
import react.css.css
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h3
import react.useState

private val scope = MainScope()

val App = FC<Props> {

    var selectedCoroutineId: Long? by useState(null)


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

        StatisticsComponent()

        div {

            css {
                display = Display.flex
                flexDirection = FlexDirection.row
            }

            CoroutinesReport() {
                onCoroutineFocus = { focusedCoroutine ->
                    selectedCoroutineId = focusedCoroutine.id
//                coroutinesFlameGraph.search(focusedCoroutine.id)
                }
                onCoroutineLoseFocus = {
                    selectedCoroutineId = null
//                coroutinesFlameGraph.clear()
                }
            }


            CoroutinesFlameGraph().fc {
                this.selectedCoroutineId = selectedCoroutineId
                onFrameClicked = {
                    scope.launch {
                        println("Selected coroutine ${it.coroutineId}")
                    }
                }

                onExit = {}
            }

        }

//        div {
//            id = "statesSequenceFlameGraph"
//        }

        SuspensionsFlameGraph().fc {
            this.selectedCoroutineId = selectedCoroutineId
            onExit = {}
            onFrameClicked = {}
        }

//        div {
//            id = "suspensionsFlameGraphContainer"
//        }

    }


}