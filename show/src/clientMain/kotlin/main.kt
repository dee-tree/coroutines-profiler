import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.profiler.show.ProfilingInfo
import react.create
import react.dom.render

val endpoint = window.location.origin

lateinit var client: HttpClient

fun main() {
    client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    val container = document.getElementById("root")!!
    container.innerHTML = "Hello, Kotlin/JS!"

    render(App.create(), container)

    println("Get flamegraph: ${flamegraph()}")

    println("Sorted: ${sorted(arrayOf(1, 2, 3))}")
    println("Sorted: ${sorted(arrayOf(3, 1,2 ))}")
}

suspend fun getProfilingInfo(): ProfilingInfo {
    return client.get<ProfilingInfo>("${endpoint}/profilingInfo")
}

@JsModule("d3-flame-graph")
@JsNonModule
@JsName("flamegraph")
external fun flamegraph(): FlameGraph


@JsModule("d3-flame-graph")
@JsNonModule
external interface FlameGraph


@JsModule("is-sorted")
@JsNonModule
external fun <T> sorted(a: Array<T>): Boolean
