package kotlinx.coroutines.profiler.app

import kotlinx.coroutines.profiler.app.matrix.Matrix
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() {
    var summaryTime = 0L
    val iters = 1

    for (i in 1..iters) {
        val iterTime = measureTimeMillis {

            runBlocking {
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
}
