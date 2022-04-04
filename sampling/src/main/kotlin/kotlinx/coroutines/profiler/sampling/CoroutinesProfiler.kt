package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.CoroutineInfo
import kotlinx.coroutines.debug.DebugProbes
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

@ExperimentalCoroutinesApi
internal class CoroutinesProfiler(private val dumpWriter: DumpWriter) {

    private var running = false

    fun attachAndRun(timeInterval: Long = 5) {
        running = true

        Runtime.getRuntime().addShutdownHook(thread(false) {
            running = false
            System.err.println("Interrupting profiler...")
            println("Interrupting profiler...")
            DebugProbes.uninstall()
            dumpWriter.stop()
            println("-".repeat(10))
            println("Total samples count: $totalSamples")
            println("Total sample time: $totalSampledTime ms")
            println("Mean sample time: ${totalSampledTime / totalSamples} ms")
            println("Total debug probes dump time: $totalDebugProbesDumpTime ms")
        })

        DebugProbes.install()
        DebugProbes.delayedCreationStackTraces = true
        DebugProbes.sanitizeStackTraces = false

        thread(isDaemon = true) {
            println("creation stack traces: ${DebugProbes.enableCreationStackTraces}")
            println("delayed creation stack traces: ${DebugProbes.delayedCreationStackTraces}")


            var dumpId = 0L
            while (running) {
                val sampleTime = measureTimeMillis {
                    sample(dumpId)
                }.also {
                    totalSampledTime += it
                    totalSamples++
                }

                val needSleep = timeInterval - sampleTime
                if (needSleep > 0)
                    Thread.sleep(timeInterval) else {
                    System.err.println("Oops... Too long sample! $sampleTime ms for Dump #${dumpId} ")
                }
                dumpId++
            }
        }
    }

    private var totalDebugProbesDumpTime = 0L
    private var totalSampledTime = 0L
    private var totalSamples = 0

    @Suppress("DEPRECATION_ERROR") // JobSupport
    private fun sample(dumpId: Long) {
        val dump: List<CoroutineInfo>
        measureTimeMillis {
            dump = DebugProbes.dumpCoroutinesInfo()
        }.also {
            if (it > 5) System.err.println("DebugProbes#dumpCoroutinesInfo worked too much! $it ms")
            totalDebugProbesDumpTime += it
        }

        val samples = transform(dump, dumpId) {
            dumpWriter.dumpNewCoroutine(it)
            println("Found new coroutine: ${it}")
        }

        println("Dump #$dumpId")
        samples.forEach {
            println(it)
        }

        println("----")

        dumpWriter.dumpSamples(samples)
    }

}