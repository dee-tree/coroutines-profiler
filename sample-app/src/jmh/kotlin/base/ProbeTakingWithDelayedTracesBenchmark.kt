package base

import kotlinx.coroutines.*
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.profiler.core.CoroutinesProfiler
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@Suppress("unused")
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@State(Scope.Thread)
abstract class ProbeTakingWithDelayedTracesBenchmark {

    @Param("true", "false")
    var useDelayedCreationStackTraces: Boolean = false

    private lateinit var globalJob: Job

    @ExperimentalCoroutinesApi
    @Setup(Level.Iteration)
    fun setup() {
        CoroutinesProfiler.DELAYED_CREATION_STACK_TRACES = useDelayedCreationStackTraces
        DebugProbes.install()

        doOnIterationSetup()

        globalJob = GlobalScope.launch {
            while (true) {
                launch {
                    doInCoroutineScope()
                }.join()
            }
        }
    }

    open fun doOnIterationSetup() = Unit

    @ExperimentalCoroutinesApi
    @TearDown(Level.Iteration)
    fun tearDown() {
        runBlocking {
            globalJob.cancelAndJoin()
        }

        DebugProbes.uninstall()

        doOnIterationTearDown()
    }

    open fun doOnIterationTearDown() = Unit

    @Benchmark
    @Fork(1)
    fun doWork(blackhole: Blackhole) {
        blackhole.consume(DebugProbes.dumpCoroutinesInfo())
    }

    abstract suspend fun doInCoroutineScope()

}