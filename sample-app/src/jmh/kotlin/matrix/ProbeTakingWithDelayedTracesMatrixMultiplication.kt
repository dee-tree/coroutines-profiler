package matrix

import base.ProbeTakingWithDelayedTracesBenchmark
import kotlinx.coroutines.profiler.app.matrix.Matrix
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.infra.Blackhole


@State(Scope.Benchmark)
@Suppress("unused")
open class ProbeTakingWithDelayedTracesMatrixMultiplication :
    ProbeTakingWithDelayedTracesBenchmark() {

    lateinit var matA: Matrix
    lateinit var matB: Matrix

    @Param("1", "10", "100")
    var coroutines: Int = 100

    override fun doOnInvocationSetup() {
        matA = Matrix.random(coroutines, 10_000)
        matB = Matrix.random(10_000, coroutines)
    }

    override suspend fun doInCoroutineScope(blackhole: Blackhole) {
        blackhole.consume(matA timesConcurrent matB)
    }

}