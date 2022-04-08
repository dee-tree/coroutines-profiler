@file:JsModule("d3-flame-graph")
@file:JsNonModule

package flamegraph

import org.w3c.dom.HTMLElement

external fun flamegraph(): FlameGraph

external interface FlameGraph {
    @nativeInvoke
    operator fun invoke(selection: Any): Any
    fun selfValue(param_val: Boolean): FlameGraph
    fun selfValue(): Boolean
    fun width(param_val: Number): FlameGraph
    fun width(): Number
    fun height(param_val: Number): FlameGraph
    fun height(): Number
    fun cellHeight(param_val: Number): FlameGraph
    fun cellHeight(): Number
    fun minFrameSize(param_val: Number): FlameGraph
    fun minFrameSize(): Number
    fun title(param_val: String): FlameGraph
    fun title(): String
    fun tooltip(param_val: Boolean): FlameGraph
    fun tooltip(): Boolean
    fun transitionDuration(param_val: Number): FlameGraph
    fun transitionDuration(): Number
    fun transitionEase(param_val: String): FlameGraph
    fun transitionEase(): String
    fun sort(param_val: Boolean): FlameGraph
    fun sort(): Boolean
    fun inverted(param_val: Boolean): FlameGraph
    fun inverted(): Boolean
    fun computeDelta(param_val: Boolean): FlameGraph
    fun computeDelta(): Boolean
    fun resetZoom()
    fun setDetailsElement(param_val: HTMLElement?): FlameGraph
    fun setDetailsElement(): HTMLElement?
    fun setDetailsHandler(): FlameGraph
    fun setSearchHandler(): FlameGraph
    fun setColorMapper(): FlameGraph
    fun setColorHue(param_val: String): FlameGraph
    fun setColorHue(): FlameGraph
    fun setSearchMatch(): FlameGraph
    fun search(term: String)
    fun clear()
    fun destroy()
}