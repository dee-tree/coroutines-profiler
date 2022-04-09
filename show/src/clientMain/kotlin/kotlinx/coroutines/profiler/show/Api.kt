package kotlinx.coroutines.profiler.show

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.browser.window
import kotlinx.coroutines.profiler.show.serialization.CoroutineProbeFrame
import kotlinx.coroutines.profiler.show.serialization.ProfilingInfo

class Api(private val client: HttpClient) {

    private val endpoint = window.location.origin

    suspend fun getProfilingInfo(): ProfilingInfo {
        return client.get<ProfilingInfo>("${endpoint}/profilingInfo")
    }

    suspend fun getStacks(): CoroutineProbeFrame {
        return client.get<CoroutineProbeFrame>("${endpoint}/stacks")
    }
}