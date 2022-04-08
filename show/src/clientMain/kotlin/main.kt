import flamegraph.flamegraph
import flamegraph.select
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.profiler.show.ProfilingInfo
import kotlinx.coroutines.profiler.show.SampleFrame
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




}

suspend fun getProfilingInfo(): ProfilingInfo {
    return client.get<ProfilingInfo>("${endpoint}/profilingInfo")
}

suspend fun getStacks(): SampleFrame {
    return client.get<SampleFrame>("${endpoint}/stacks")
}

//fun SampleFrame.toJSON(): JSON {
//    return JSON.
//}
