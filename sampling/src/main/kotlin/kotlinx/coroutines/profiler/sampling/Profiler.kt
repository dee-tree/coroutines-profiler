package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.profiler.sampling.Agent.foldAll
import kotlin.concurrent.thread

@ExperimentalCoroutinesApi
object Profiler {

    fun attachAndRun(timeInterval: Long = 5) {
        val attachedThread = Thread.currentThread()

        thread {
            DebugProbes.install()

            while (attachedThread.isAlive) {

                println("Coroutines dump at ${System.currentTimeMillis()}")
                val dump = DebugProbes.dumpCoroutinesInfo()

                dump.forEach {
                    println("Coroutine: ${it}, job: ${it.job!!.foldAll()}")
                    it.state
                    println(it.lastObservedStackTrace().joinToString("\n\t") { trace -> trace.toString() })
                }

                println("-".repeat(10))

                Thread.sleep(timeInterval)
            }

            DebugProbes.uninstall()
        }
    }
}