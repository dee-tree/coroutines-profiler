import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import kotlinx.browser.document
import kotlinx.coroutines.profiler.show.Api
import react.create
import react.dom.render


lateinit var api: Api

fun main() {
    val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    api = Api(client)


    val container = document.getElementById("root")!!

    render(App.create(), container)

}
