import csstype.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.profiler.show.serialization.CoroutineProbeFrame
import kotlinx.coroutines.profiler.show.ui.*
import react.FC
import react.Props
import react.css.css
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h3
import react.useState

private val scope = MainScope()

val App = FC<Props> {

    var selectedCoroutineFrame: CoroutineProbeFrame? by useState(null)
    var selectedCoroutineId: Long? by useState(null)

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
                    }
                    onSelectionCleared = {
                        selectedCoroutineId = null
                    }
                }

                if (selectedCoroutineId != null || selectedCoroutineFrame != null) {
                    CoroutineInfo {
                        this.coroutineId = selectedCoroutineId
                        this.probeFrame = selectedCoroutineFrame
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

                div {
                    id = "coroutinesFlameGraphContainer"


                    CoroutinesFlameGraph {
                        coroutineIdToSearch = selectedCoroutineId
                        onFrameClicked = {
                            println("Selected probe frame for coroutine ${it.coroutineId}")
                            selectedCoroutineFrame = it

                        }

                        onExit = {
                            selectedCoroutineFrame = null
                        }
                    }

                }

                SuspensionsFlameGraph {
                    this.coroutineId = selectedCoroutineId
                    this.probeFrame = selectedCoroutineFrame
                }

                selectedCoroutineId?.let { coroId ->
                    ThreadsFlameGraph {
                        this.coroutineId = coroId
                    }
                }

                StatisticsComponent()

            }


        }

    }


}