package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes
import kotlin.concurrent.thread

@ExperimentalCoroutinesApi
object Profiler {

    private val coroutinesInfo = CoroutinesProfileInfoOwner()

    fun attachAndRun(timeInterval: Long = 5) {
        val attachedThread = Thread.currentThread()

        thread {
            DebugProbes.install()

            while (attachedThread.isAlive) {
                sample()
                Thread.sleep(timeInterval)
            }

            coroutinesInfo.printReport()
            DebugProbes.uninstall()
        }
    }

    private fun sample() {
        val dump = DebugProbes.dumpCoroutinesInfo()
        coroutinesInfo.sample(dump)
    }

}