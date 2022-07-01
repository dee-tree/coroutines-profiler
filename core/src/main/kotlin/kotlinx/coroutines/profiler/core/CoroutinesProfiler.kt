package kotlinx.coroutines.profiler.core

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.profiler.core.agent.args.DEFAULT_SHOULD_INTERNAL_STATISTICS_BE_COLLECTED
import kotlinx.coroutines.profiler.core.data.CoroutinesDump
import kotlinx.coroutines.profiler.core.data.statistics.InternalProfilingStatistics
import kotlinx.coroutines.profiler.core.internals.Sampler
import kotlinx.coroutines.profiler.core.internals.SamplerImpl
import kotlinx.coroutines.profiler.core.internals.StatsCollectingSamplerImpl
import kotlinx.coroutines.profiler.core.writers.DumpWriter
import kotlin.concurrent.thread
import kotlin.contracts.ExperimentalContracts
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
class CoroutinesProfiler(
    private val dumpWriter: DumpWriter,
    val collectInternalStatistics: Boolean = DEFAULT_SHOULD_INTERNAL_STATISTICS_BE_COLLECTED
) {

    companion object {
        public var LAZY_CREATION_STACK_TRACES = true
    }

    @ExperimentalTime
    @ExperimentalContracts
    private val sampler: Sampler = if (collectInternalStatistics) StatsCollectingSamplerImpl() else SamplerImpl()
    private var running = false

    @ExperimentalContracts
    @ExperimentalTime
    fun attachAndRun(timeInterval: Int = 5) {
        running = true

        Runtime.getRuntime().addShutdownHook(thread(false) {
            running = false
            System.err.println("Interrupting profiler...")
            println("Interrupting profiler...")
            DebugProbes.uninstall()

            if (collectInternalStatistics) {

                dumpWriter.setInternalStatistics(
                    InternalProfilingStatistics(
                        sampler.getTakingStatistics()!!,
                        sampler.getHandlingStatistics()!!
                    )
                )
            }

            dumpWriter.stop()
        })

        DebugProbes.install()
        DebugProbes.lazyCreationStackTraces = LAZY_CREATION_STACK_TRACES
        DebugProbes.sanitizeStackTraces = true

        thread(isDaemon = true) {
            println("creation stack traces: ${DebugProbes.enableCreationStackTraces}")
            println("sanitize stack traces: ${DebugProbes.sanitizeStackTraces}")
            println("lazy creation stack traces: ${DebugProbes.lazyCreationStackTraces}")

            while (running) {
                val dump: CoroutinesDump

                val probeHandlingTime = measureTimeMillis {
                    dump = sampler.probe { newCoroutine ->
                        dumpWriter.writeNewCoroutine(newCoroutine)
                        println("Found new coroutine: ${newCoroutine}")
                    }
                }

                dumpWriter.writeDump(dump)


                val needSleep = timeInterval - probeHandlingTime
                if (needSleep > 0)
                    Thread.sleep(timeInterval.toLong())
            }
        }
    }

}