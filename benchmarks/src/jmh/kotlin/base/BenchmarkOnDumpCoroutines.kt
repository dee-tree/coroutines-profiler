package base

import kotlinx.coroutines.*
import kotlinx.coroutines.debug.DebugProbes
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole

abstract class BenchmarkOnDumpCoroutines : BaseBenchmark() {

    abstract fun runJob(): Job

    private var job: Job? = null

    @Setup(Level.Invocation)
    fun onInvocationSetup() {
        job = runJob()
    }

    @TearDown(Level.Invocation)
    fun onInvocationTearDown() {
        if (job!!.isCompleted) throw IllegalStateException("Job cancelled before invocation tear down!")
        job!!.cancel("benchmark invocation finished")
    }

    @Param("false", "true")
    var getCreationStackTrace: Boolean = false

    override fun run(blackhole: Blackhole) {
        if (this.mode == Modes.NO_PROBES) return

        val dump = DebugProbes.dumpCoroutinesInfo()

        if (getCreationStackTrace) {
            dump.forEach {
                blackhole.consume(it.creationStackTrace)
            }
        } else {
            blackhole.consume(dump)
        }
    }
}


open class BenchmarkLongDelayOnDumpCoroutines : BenchmarkOnDumpCoroutines() {
    override fun runJob(): Job = GlobalScope.launch {
        delay(Long.MAX_VALUE)
    }
}

open class BenchmarkLotOfDelaysOnDumpCoroutines : BenchmarkOnDumpCoroutines() {
    override fun runJob(): Job = GlobalScope.launch {
        repeat(10_000) {
            delay(10)
        }
    }
}