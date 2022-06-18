package delay

import base.ProbeTakingWithDelayedTracesBenchmark
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.openjdk.jmh.annotations.Param


@Suppress("unused")
open class ProbeTakingWithDelayedTracesDelay :
    ProbeTakingWithDelayedTracesBenchmark() {


    @Param("1", "10", "100")
    var coroutines: Int = 1

    @Param("1", "10", "100", "1000")
    var delayMillis: Long = 1000


    override suspend fun doInCoroutineScope() {
        repeat(coroutines) {
            coroutineScope {
                launch {
                    delay(delayMillis)
                }
            }
        }
    }

}