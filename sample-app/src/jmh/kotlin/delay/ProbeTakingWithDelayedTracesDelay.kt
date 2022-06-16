package delay

import base.ProbeTakingWithDelayedTracesBenchmark
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.infra.Blackhole


@State(Scope.Benchmark)
@Suppress("unused")
open class ProbeTakingWithDelayedTracesDelay :
    ProbeTakingWithDelayedTracesBenchmark() {


    @Param("1", "10", "100")
    var coroutines: Int = 100

    @Param("1", "10", "100", "1000")
    var delayMillis: Long = 1


    override suspend fun doInCoroutineScope(blackhole: Blackhole) {
        repeat(coroutines) {
            coroutineScope {
                launch {
                    delay(delayMillis)
                }
            }
        }
    }

}