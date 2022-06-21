package kotlinx.coroutines.profiler.show

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.browser.window
import kotlinx.coroutines.profiler.core.data.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.core.data.State
import kotlinx.coroutines.profiler.core.data.statistics.ProfilingStatistics
import kotlinx.coroutines.profiler.show.serialization.CoroutineProbeFrame
import kotlinx.coroutines.profiler.show.serialization.CoroutineSuspensionsFrame
import kotlinx.coroutines.profiler.show.serialization.CoroutineThreadsFrame

class Api(private val client: HttpClient) {

    private val endpoint = window.location.origin

    suspend fun getProfilingStatistics(): ProfilingStatistics {
        return client.get<ProfilingStatistics>("${endpoint}/profilingStatistics")
    }

    suspend fun getStacks(): CoroutineProbeFrame {
        return client.get<CoroutineProbeFrame>("${endpoint}/stacks")
    }

    suspend fun getAllCoroutinesIds(): List<Long> {
        return client.get("${endpoint}/coroutinesIds")
    }

    suspend fun getCoroutineReport(coroutineId: Long): ProfilingCoroutineInfo {
        return client.get<ProfilingCoroutineInfo>("${endpoint}/coroutineReport") {
            parameter("id", coroutineId)
        }
    }

    suspend fun getCoroutineStateRangesCount(coroutineId: Long, state: State): Int {
        return client.get<Int>("${endpoint}/coroutineSwitchedTimesAtState") {
            parameter("id", coroutineId)
            parameter("state", state)
        }
    }

    suspend fun getCoroutineSuspensionsCountWithSameStacktracesLikeProbeFrame(coroutineId: Long, probeId: Int): Int {
        return client.get<Int>("${endpoint}/coroutineSuspensionsCountWithStacktraceInProbe") {
            parameter("id", coroutineId)
            parameter("probeId", probeId)
        }
    }


    suspend fun getCoroutineProbesCount(coroutineId: Long, state: State): Int {
        return client.get<Int>("${endpoint}/coroutineProbesCount") {
            parameter("id", coroutineId)
            parameter("state", state)
        }
    }

    suspend fun getSuspensionsStackTrace(coroutineId: Long): CoroutineSuspensionsFrame {
        return client.get<CoroutineSuspensionsFrame>("${endpoint}/suspensionsStackTrace/${coroutineId}") {
        }
    }

    suspend fun getSuspensionsStackTrace(): CoroutineSuspensionsFrame {
        return client.get<CoroutineSuspensionsFrame>("${endpoint}/suspensionsStackTrace")
    }

    suspend fun getThreadsFrame(coroutineId: Long): CoroutineThreadsFrame {
        return client.get<CoroutineThreadsFrame>("${endpoint}/threads/${coroutineId}")
    }
}