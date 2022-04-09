package kotlinx.coroutines.profiler.show.routes

import io.ktor.http.content.*
import io.ktor.routing.*

fun Route.staticRoute() {
    static("/") {
        resources("")
        resource("/", "index.html")
    }

}