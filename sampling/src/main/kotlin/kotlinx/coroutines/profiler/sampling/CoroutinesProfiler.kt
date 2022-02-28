package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.CoroutineInfo
import kotlinx.coroutines.debug.DebugProbes
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

@ExperimentalCoroutinesApi
object CoroutinesProfiler {

    private val coroutinesInfo = ProfileCoroutineInfo.CoroutinesProfileInfoOwner()

    fun attachAndRun(timeInterval: Long = 5) {
        val attachedThread = Thread.currentThread()

        thread {
            DebugProbes.install()
//            DebugProbes.enableCreationStackTraces = false
            DebugProbes.delayedCreationStackTraces = true
            println("creation stack traces: ${DebugProbes.enableCreationStackTraces}")
            println("delayed creation stack traces: ${DebugProbes.delayedCreationStackTraces}")

            var totalSampledTime = 0L
            var totalSamples = 0

            var dumpNumber = 0
            while (attachedThread.isAlive) {
                val sampleTime = measureTimeMillis {
                    sample()
                }.also {
                    totalSampledTime += it
                    totalSamples++
                }

                val needSleep = timeInterval - sampleTime
                if (needSleep > 0)
                    Thread.sleep(timeInterval) else {
                    System.err.println("Oops... Too long sample! $sampleTime ms for Dump #${dumpNumber++} ")
                }
            }

            DebugProbes.uninstall()
            coroutinesInfo.printReport()

            println("-".repeat(10))
            println("Total samples count: $totalSamples")
            println("Total sample time: $totalSampledTime ms")
            println("Mean sample time: ${totalSampledTime / totalSamples} ms")
            println("Total debug probes dump time: $totalDebugProbesDumpTime ms")
        }
    }

    private var totalDebugProbesDumpTime = 0L

    private fun sample() {
        val dump: List<CoroutineInfo>
        measureTimeMillis {
            dump = DebugProbes.dumpCoroutinesInfo()
        }.also {
            if (it > 5) System.err.println("DebugProbes#dumpCoroutinesInfo worked too much! $it ms")
            totalDebugProbesDumpTime += it
        }

        coroutinesInfo.sample(dump)
    }

    private fun nowFormatted() = DateTimeFormatter.ofPattern("yyyy_MM_dd-hh_mm_ss").format(LocalDateTime.now())
}