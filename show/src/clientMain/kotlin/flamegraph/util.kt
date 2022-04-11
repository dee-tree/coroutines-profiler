package flamegraph

import kotlinext.js.asJsObject

const val HIGHLIGHTED_COLOR = "#6c84d0"
const val CREATED_COLOR = "#FFB266"
const val RUNNING_COLOR = "#70ff66"
const val SUSPENDED_COLOR = "#FF6666"
const val DEFAULT_COLOR = "#AAAAAA"


fun coroutineStateColorMapper(d: Any, originalColor: String): String {// d, originalColor ->
    val d = d.asJsObject().valueOf()

    return if (d.highlight) HIGHLIGHTED_COLOR else when (d.data.state) {
        "CREATED" -> CREATED_COLOR
        "RUNNING" -> RUNNING_COLOR
        "SUSPENDED" -> SUSPENDED_COLOR
        else -> DEFAULT_COLOR
    }
}

fun coroutineFrameLabel(d: Any): String {
    val d = d.asJsObject().valueOf()

    return "name: " + d.data.name + "\n" +
            "value: " + d.value + "\n" +
            "probes: " + d.data.probesCount + "\n" +
            "state: " + d.data.state + "\n" +
            if (d.data.state == "RUNNING") "thread: ${d.data.thread}\n" else ""
}
