@file:JsModule("d3")
@file:JsNonModule

package flamegraph

import kotlinext.js.Object

external fun select(elementId: String): Selection

external class Selection {
    fun datum(data: Object): Selection
//    fun datum(data: JSON): Selection
    fun call(flameGraph: FlameGraph): Selection
}