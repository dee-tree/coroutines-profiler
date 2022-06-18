package matrix

import base.ProbeTakingWithDelayedTracesBenchmark
import kotlinx.coroutines.profiler.app.matrix.Matrix
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.Param


@BenchmarkMode(Mode.SampleTime)
@Suppress("unused")
open class ProbeTakingWithDelayedTracesMatrixMultiplication :
    ProbeTakingWithDelayedTracesBenchmark() {

    lateinit var matA: Matrix
    lateinit var matB: Matrix

    @Param("1", "10", "100", "1000")
    var coroutines: Int = 100

    override fun doOnIterationSetup() {
        matA = Matrix.random(coroutines, 100_000)
        matB = Matrix.random(100_000, 1_000)
    }

    override suspend fun doInCoroutineScope() {
        matA timesConcurrent matB
    }

}