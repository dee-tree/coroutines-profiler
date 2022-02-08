package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes
import kotlin.concurrent.thread

@ExperimentalCoroutinesApi
object Profiler {

    private val coroutinesInfo = CoroutinesProfileInfoOwner()

    fun attachAndRun(timeInterval: Long = 5) {
        val begin = System.currentTimeMillis()

        val attachedThread = Thread.currentThread()

        thread {
            DebugProbes.install()
//            DebugProbes.enableCreationStackTraces = false

            while (attachedThread.isAlive) {
                sample()
                Thread.sleep(timeInterval)
            }

//            coroutinesInfo.printReport()
            DebugProbes.uninstall()

            println("Profiler worked ${System.currentTimeMillis() - begin} ms")
        }
    }


    var dumpNumber = 0

    var lastTime = System.currentTimeMillis()

    private fun sample() {
        val dump = DebugProbes.dumpCoroutinesInfo()
        coroutinesInfo.sample(dump)
        println("Dump ${dumpNumber++}. Coroutines: ${dump.size}, time: ${System.currentTimeMillis() - lastTime}")
        lastTime = System.currentTimeMillis()
    }

}