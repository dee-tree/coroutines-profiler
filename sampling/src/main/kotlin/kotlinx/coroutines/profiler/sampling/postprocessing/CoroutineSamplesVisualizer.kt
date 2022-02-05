package kotlinx.coroutines.profiler.sampling.postprocessing

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.sampling.CoroutineProfileInfo

@ExperimentalCoroutinesApi
class CoroutineSamplesVisualizer(coroutineProfileInfo: CoroutineProfileInfo) {

    private val coroutineId = coroutineProfileInfo.id

    private val samples = coroutineProfileInfo.samples

    fun threadsInfo(): String {
        val threads = mutableListOf<ThreadCounter>()

        samples.forEach { sample ->

            val currentLast = threads.lastOrNull()
            if (currentLast != null && currentLast.thread == sample.thread) {
                currentLast.samples++
                currentLast.lastSampledTime = sample.creationTime
            } else {
                sample.thread?.let { threads.add(ThreadCounter(it, 1, sample.creationTime)) }
            }
        }

        return threads.joinToString(" | ")
    }

    private class ThreadCounter(val thread: Thread, var samples: Int, val firstSampledTime: Long) {

        var lastSampledTime: Long = firstSampledTime

        override fun toString(): String = "$thread: samples = $samples, executed = ${lastSampledTime - firstSampledTime} ms"
    }
}