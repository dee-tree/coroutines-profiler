import csstype.*
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.show.serialization.CoroutineProbeFrame
import kotlinx.coroutines.profiler.show.ui.*
import kotlinx.dom.clear
import react.FC
import react.Props
import react.create
import react.css.css
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h3
import react.dom.render
import react.dom.unmountComponentAtNode

private val scope = MainScope()

val App = FC<Props> {

    var selectedCoroutineFrame: CoroutineProbeFrame? = null
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

                div {
                    id = "coroutineProbeFrameContainer"
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
                            selectedCoroutineFrame = it
                            println("Selected coroutine ${it.coroutineId}")

                            render(CoroutineProbeFrameInfo.create() {
                                this.probeFrame = it
                            }, document.getElementById("coroutineProbeFrameContainer")!!)

                            document.getElementById("coroutineProbeFrameContainer")
                        }
                    }

                    onExit = {
                        selectedCoroutineFrame = null

                        unmountComponentAtNode(document.getElementById("coroutineProbeFrameContainer")!!)
                    }
                }

                suspensionsFlameGraph.fc {
                    onExit = {}
                    onFrameClicked = {}
                }
            }


        }

    }


}