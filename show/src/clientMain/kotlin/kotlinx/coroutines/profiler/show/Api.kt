package kotlinx.coroutines.profiler.show

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.browser.window
import kotlinx.coroutines.profiler.core.data.statistics.ProfilingStatistics
import kotlinx.coroutines.profiler.show.serialization.CoroutineProbeFrame
import kotlinx.coroutines.profiler.show.serialization.CoroutineSuspensionsFrame

class Api(private val client: HttpClient) {

    private val endpoint = window.location.origin

    suspend fun getProfilingStatistics(): ProfilingStatistics {
        return client.get<ProfilingStatistics>("${endpoint}/profilingStatistics")
    }

    suspend fun getStacks(): CoroutineProbeFrame {
        return client.get<CoroutineProbeFrame>("${endpoint}/stacks")
    }


    suspend fun getSuspensionsStackTrace(coroutineId: Long): CoroutineSuspensionsFrame {
        println("Intended to request suspensions stack trace for coroutine $coroutineId")
        return client.get<CoroutineSuspensionsFrame>("${endpoint}/suspensionsStackTrace") {
            parameter("id", coroutineId)
        }
    }
}