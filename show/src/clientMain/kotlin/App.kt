import csstype.*
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

private val scope = MainScope()

val App = FC<Props> {

    var selectedCoroutineId: Long? = null
    val coroutinesFlameGraph by lazy { CoroutinesFlameGraph() }
    val suspensionsFlameGraph by lazy { SuspensionsFlameGraph() }

    h3 {
        css {
            textAlign = TextAlign.center
        }
        +"Coroutines profiler"
    }

    div {
        id = "content"

        css {
            paddingTop = 1.vh
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
        }


        div {

            css {
                display = Display.flex
                flexDirection = FlexDirection.row
            }

            div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                }

                CoroutinesReport() {
                    onCoroutineSelected = { focusedCoroutine ->
                        selectedCoroutineId = focusedCoroutine.id
                        coroutinesFlameGraph.search(focusedCoroutine.id)
                        suspensionsFlameGraph.showCoroutine(focusedCoroutine.id)
                    }
                    onSelectionCleared = {
                        selectedCoroutineId = null
                        coroutinesFlameGraph.clear()
                        suspensionsFlameGraph.clear()
                    }
                }
            }


            div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.column

                    alignSelf = AlignSelf.center
                }

                StatisticsComponent()


                coroutinesFlameGraph.fc {
                    onFrameClicked = {
                        scope.launch {
                            println("Selected coroutine ${it.coroutineId}")
                        }
                    }

                    onExit = {}
                }

                suspensionsFlameGraph.fc {
                    onExit = {}
                    onFrameClicked = {}
                }
            }


        }

    }


}