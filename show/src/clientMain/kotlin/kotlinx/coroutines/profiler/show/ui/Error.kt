package kotlinx.coroutines.profiler.show.ui

import csstype.*
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.browser.window
import react.ChildrenBuilder
import react.FC
import react.Props
import react.css.css
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.p

private val endpoint = window.location.origin


val Error = FC<ErrorProps> { props ->
    div {

        css {
            paddingTop = 10.vh
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
        }

        div {
            id = "errorMsg"

            css {
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center

                width = 50.vw
                padding = 2.em

                borderRadius = 1.em
                borderStyle = LineStyle.solid
                borderColor = Color("#BB6464")
                borderWidth = 2.px
            }

            h2 {
                +"Ooops... You unexpectedly faced with error"
            }

            img {
                src = "error.png"

                css {
                    width = 5.vw
                }
            }

            when (props.error) {
                is ClientRequestException -> errorToHtmlElement(props.error as ClientRequestException)
                else -> {
                    p {
                        +props.error.toString()
                    }
                }

            }
        }

    }
}

external interface ErrorProps : Props {
    var error: Throwable
}

private fun ChildrenBuilder.errorToHtmlElement(exception: ClientRequestException) {
    h2 {
        +exception.response.status.toString()
        css {
            color = Color("#BB6464")
            paddingBottom = 1.em
        }
    }

    p {
        +"Invalid request: ${exception.response.request.url}"
    }

    p {
        + exception.message
    }

    when (exception.response.request.url.toString()) {
        "${endpoint}/profilingInfo" -> when (exception.response.status.value) {
            404 -> p {
                +"Supposed reason: Files with profiling results were not received or its path is not valid"
            }
        }

    }
}