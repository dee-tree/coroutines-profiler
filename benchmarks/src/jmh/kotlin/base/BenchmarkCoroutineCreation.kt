package base

import kotlinx.coroutines.*
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole

open class BenchmarkRunBlocking : BaseBenchmark() {

    override fun run(blackhole: Blackhole) {
        runBlocking {
            blackhole.consume(null)
        }
    }
}

open class BenchmarkRunBlockingLaunch : BaseBenchmark() {

    override fun run(blackhole: Blackhole) {
        runBlocking {
            launch {
                blackhole.consume(null)
            }
        }
    }
}

open class BenchmarkLaunch : BaseBenchmark() {

    override fun run(blackhole: Blackhole) {
        val job = GlobalScope.launch {
            blackhole.consume(null)

        }
    }
}
