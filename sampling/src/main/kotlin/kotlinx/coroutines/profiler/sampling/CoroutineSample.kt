package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.CoroutineInfo
import kotlinx.coroutines.debug.State

@Deprecated("")
@ExperimentalCoroutinesApi
class CoroutineSample(info: CoroutineInfo) {

    val creationTime = System.currentTimeMillis()

    val state: State = info.state
    val id = info.id
    val thread = info.lastObservedThread

    val currentStackTrace = info.lastObservedStackTrace()
}