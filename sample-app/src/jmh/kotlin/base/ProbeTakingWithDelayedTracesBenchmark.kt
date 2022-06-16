package base

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.launch
import kotlinx.coroutines.profiler.core.CoroutinesProfiler
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@Suppress("unused")
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@State(Scope.Benchmark)
abstract class ProbeTakingWithDelayedTracesBenchmark {

    @Param("true", "false")
    var useDelayedCreationStackTraces: Boolean = false

    @ExperimentalCoroutinesApi
    @Setup(Level.Invocation)

    fun setup() {
        CoroutinesProfiler.DELAYED_CREATION_STACK_TRACES = useDelayedCreationStackTraces
        DebugProbes.install()

        doOnInvocationSetup()
    }

    open fun doOnInvocationSetup() = Unit

    @ExperimentalCoroutinesApi
    @TearDown(Level.Invocation)
    fun tearDown() {
        DebugProbes.uninstall()

        doOnInvocationTearDown()
    }

    open fun doOnInvocationTearDown() = Unit

    @Benchmark
    @Fork(1)
    fun doWork(blackhole: Blackhole) {
        var finished = false
        GlobalScope.launch {
            launch {
                doInCoroutineScope(blackhole)
            }.join()
            finished = true
        }

        while (!finished) {
            DebugProbes.dumpCoroutinesInfo()
        }
    }

    abstract suspend fun doInCoroutineScope(blackhole: Blackhole)

}