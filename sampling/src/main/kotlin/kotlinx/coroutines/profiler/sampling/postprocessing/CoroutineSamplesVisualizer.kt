package kotlinx.coroutines.profiler.sampling.postprocessing

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.State
import kotlinx.coroutines.profiler.sampling.ProfileCoroutineInfo

@ExperimentalCoroutinesApi
internal class CoroutineSamplesVisualizer(coroutineProfileInfo: ProfileCoroutineInfo) {

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

    fun statesInfo(): String {
        val states = mutableListOf<StateCounter>()

        samples.forEach { sample ->
            val currentLast = states.lastOrNull()
            if (currentLast != null && currentLast.state == sample.state) {
                currentLast.samples++
                currentLast.lastSampledTime = sample.creationTime
            } else {
                states.add(StateCounter(sample.state, 1, sample.creationTime))
            }
        }
        return states.joinToString(" | ")
    }

    private class ThreadCounter(val thread: Thread, var samples: Int, val firstSampledTime: Long) {

        var lastSampledTime: Long = firstSampledTime

        override fun toString(): String = "$thread: samples = $samples, executed = ${lastSampledTime - firstSampledTime} ms"
    }

    private class StateCounter(val state: State, var samples: Int, val firstSampledTime: Long) {

        var lastSampledTime: Long = firstSampledTime

        override fun toString(): String = "$state: samples = $samples, executed = ${lastSampledTime - firstSampledTime} ms"
    }


}