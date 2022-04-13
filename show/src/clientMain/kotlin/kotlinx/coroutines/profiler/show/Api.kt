package kotlinx.coroutines.profiler.show

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.browser.window
import kotlinx.coroutines.profiler.core.data.statistics.ProfilingStatistics
import kotlinx.coroutines.profiler.show.serialization.CoroutineProbeFrame

class Api(private val client: HttpClient) {

    private val endpoint = window.location.origin

    suspend fun getProfilingStatistics(): ProfilingStatistics {
        return client.get<ProfilingStatistics>("${endpoint}/profilingStatistics")
    }

    suspend fun getStacks(): CoroutineProbeFrame {
        return client.get<CoroutineProbeFrame>("${endpoint}/stacks")
    }

    suspend fun coroutine(coroutineId: Long): HttpResponse {
        return client.get("${endpoint}/coroutine") {
            parameter("id", coroutineId)
        }
    }
}