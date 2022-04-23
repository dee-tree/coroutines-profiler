package flamegraph

import kotlinext.js.asJsObject

const val HIGHLIGHTED_COLOR = "#6c84d0"
const val CREATED_COLOR = "#FFB266"
const val RUNNING_COLOR = "#00ED14"
const val SUSPENDED_COLOR = "#9915FF"
const val DEFAULT_COLOR = "#AAAAAA"

const val ALPHA_CREATED_COLOR = "#FFB26690"
const val ALPHA_RUNNING_COLOR = "#00ED1490"
const val ALPHA_SUSPENDED_COLOR = "#9915FF90"


fun coroutineStateColorMapper(d: Any, originalColor: String): String {
    val d = d.asJsObject().valueOf()

    return when (d.highlight) {
        true -> when (d.data.state) {
            "CREATED" -> CREATED_COLOR
            "RUNNING" -> RUNNING_COLOR
            "SUSPENDED" -> SUSPENDED_COLOR
            else -> DEFAULT_COLOR
        }
        else -> when (d.data.state) {
            "CREATED" -> ALPHA_CREATED_COLOR
            "RUNNING" -> ALPHA_RUNNING_COLOR
            "SUSPENDED" -> ALPHA_SUSPENDED_COLOR
            else -> DEFAULT_COLOR
        }
    }
}

fun suspensionsColorMapper(d: Any, originalColor: String): String {
    val d = d.asJsObject().valueOf()

    return if (d.highlight) SUSPENDED_COLOR else ALPHA_SUSPENDED_COLOR
}

fun coroutineFrameLabel(d: Any): String {
    val d = d.asJsObject().valueOf()

    return "name: " + d.data.name + "\n" +
            "value: " + d.value + "\n" +
            "probes: " + d.data.probesCount + "\n" +
            "state: " + d.data.state + "\n" +
            if (d.data.state == "RUNNING") "thread: ${d.data.thread}\n" else ""
}

fun coroutineIdSearchMatch(d: Any, id: Long): Boolean {
    val d = d.asJsObject().valueOf()
    return d.data.id == id
}