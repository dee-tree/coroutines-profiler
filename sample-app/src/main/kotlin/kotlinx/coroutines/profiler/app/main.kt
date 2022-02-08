package kotlinx.coroutines.profiler.app

import kotlinx.coroutines.profiler.app.matrix.Matrix
import kotlinx.coroutines.runBlocking


fun main() {
    var summaryTime = 0L
    val iters = 10

    for (i in 1..iters) {
        val begin = System.nanoTime()

        runBlocking {
            calculateMatrix()
        }
        val iterTime = System.nanoTime() - begin
        summaryTime += iterTime / 1_000_000 // (ms)
    }

    println("Mean execution of cycle: ${summaryTime / iters}")
    println("program execution time: ${summaryTime} ms")
}


suspend fun calculateMatrix() {
    // coroutines count: rows in the first matrix
    Matrix.random(100, 1_000_000) timesConcurrent  Matrix.random(1_000_000, 10)
}
