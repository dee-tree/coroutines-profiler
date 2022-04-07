import csstype.EmptyCells.Companion.show
import io.ktor.client.*
import kotlinx.browser.document

fun main() {

    println("Main in js!")
//    val client = HttpClient()

    document.bgColor = "#AAAAAA"
//    val response = client.get<HttpResponse>("localhost:9090/")
    document.getElementById("root")?.innerHTML = "Hello, Kotlin/JS!"


//    println(elementAt(listOf(1,2,3), 1))

//    flamegraph()
    println("hi: ${hi()}")
//    println(js("hi.call()"))

    println("Sorted: ${sorted(arrayOf(1, 2, 3))}")
    println("Sorted: ${sorted(arrayOf(3, 1,2 ))}")
}

external fun alert(message: Any?): Unit

//@JsModule("coroutines-profiler-show-client")
@JsName("hi")
external fun hi()

@JsModule("d3-flame-graph")
@JsNonModule
external fun flamegraph()



@JsModule("is-sorted")
@JsNonModule
external fun <T> sorted(a: Array<T>): Boolean
