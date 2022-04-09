package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.profiler.sampling.agent.args.DEFAULT_SHOULD_INTERNAL_STATISTICS_BE_COLLECTED
import kotlinx.coroutines.profiler.sampling.internals.ProfilingCoroutineDump
import kotlinx.coroutines.profiler.sampling.internals.Sampler
import kotlinx.coroutines.profiler.sampling.writers.DumpWriter
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

@ExperimentalCoroutinesApi
class CoroutinesProfiler(
    private val dumpWriter: DumpWriter,
    private val sampler: Sampler,
    val collectInternalStatistics: Boolean = DEFAULT_SHOULD_INTERNAL_STATISTICS_BE_COLLECTED
) {

    private var running = false

    fun attachAndRun(timeInterval: Int = 5) {
        running = true

        Runtime.getRuntime().addShutdownHook(thread(false) {
            running = false
            System.err.println("Interrupting profiler...")
            println("Interrupting profiler...")
            DebugProbes.uninstall()

            if (collectInternalStatistics) {
                sampler.getInternalStatistics()!!.also {
                    it.maxProbeHandlingTimeMillis = maxProbeHandlingTime
                    it.meanProbeHandlingTimeMillis = totalProbeHandlingTime / totalProbes
                    dumpWriter.setInternalStatistics(it.build())
                }
            }

            dumpWriter.stop()
            println("-".repeat(10))
            println("Total probes count: $totalProbes")
            println("Total probe handling time: $totalProbeHandlingTime ms")
            println("Mean probe handling time: ${totalProbeHandlingTime / totalProbes} ms")
        })

        DebugProbes.install()
        DebugProbes.delayedCreationStackTraces = true
        DebugProbes.sanitizeStackTraces = true

        thread(isDaemon = true) {
            println("creation stack traces: ${DebugProbes.enableCreationStackTraces}")
            println("delayed creation stack traces: ${DebugProbes.delayedCreationStackTraces}")

            while (running) {
                val dump: ProfilingCoroutineDump

                val probeHandlingTime = measureTimeMillis {
                    dump = sampler.probe { newCoroutine ->
                        dumpWriter.writeNewCoroutine(newCoroutine)
                        println("Found new coroutine: ${newCoroutine}")
                    }
                }.also {
                    val handleTime = it.toInt()
                    totalProbes++
                    totalProbeHandlingTime += handleTime
                    if (it > maxProbeHandlingTime) maxProbeHandlingTime = handleTime
                }


                dumpWriter.writeDump(dump)


                val needSleep = timeInterval - probeHandlingTime
                if (needSleep > 0)
                    Thread.sleep(timeInterval.toLong())
            }
        }
    }

    private var totalProbeHandlingTime = 0
    private var maxProbeHandlingTime = 0
    private var totalProbes = 0


}