package kotlinx.coroutines.profiler.app

import kotlinx.coroutines.*
import kotlinx.coroutines.profiler.app.matrix.Matrix
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.system.measureTimeMillis

fun main() {
    println("PID: ${ProcessHandle.current().pid()}")
    var summaryTime = 0L
    val iters = 1

    for (i in 1..iters) {

        val iterTime = measureTimeMillis {

            runBlocking {
//                repeat(3) {
//                    launch {
//                        withContext(Dispatchers.Default) {
//                            delay(2000)
//
//                        }
//                    }
//                }
//                delay(1000)
//
//                List(50_000) { it }.shuffled().sorted()

//                Thread.sleep(100)

                calculateMatrix()
            }

        }
        summaryTime += iterTime // (ms)
    }

    println("Mean execution of cycle: ${summaryTime / iters}")
    println("program execution time: ${summaryTime} ms")
}


suspend fun calculateMatrix() {
    // coroutines count: rows in the first matrix
    Matrix.random(10, 1_000_000) timesConcurrent Matrix.random(1_000_000, 10)
//    Thread.sleep(50000)
}
