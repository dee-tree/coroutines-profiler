package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.lang.instrument.Instrumentation

object Agent {

    @ExperimentalCoroutinesApi
    @JvmStatic
    fun premain(args: String?, instrumentation: Instrumentation) {
        val mainThread = Thread.currentThread()
        println("agent: run at thread: ${mainThread}")
        println("agent: PID: " + ProcessHandle.current().pid())

        CoroutinesProfiler.attachAndRun(5)
    }

}