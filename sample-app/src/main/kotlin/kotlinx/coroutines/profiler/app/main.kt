package kotlinx.coroutines.profiler.app

import kotlinx.coroutines.profiler.app.matrix.Matrix
import kotlinx.coroutines.runBlocking


fun main() {
    val begin = System.currentTimeMillis()
    runBlocking {
        calculateMatrix()
    }

    println("program execution time: ${System.currentTimeMillis() - begin} ms")
}


suspend fun calculateMatrix() {
    // coroutines count: rows in the first matrix
    Matrix.random(5, 10_000) timesConcurrent  Matrix.random(10_000, 1_000)
}
