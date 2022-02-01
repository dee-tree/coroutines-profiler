package kotlinx.coroutines.profiler.app

import kotlinx.coroutines.delay
import kotlinx.coroutines.profiler.app.matrix.Matrix
import kotlinx.coroutines.runBlocking


fun main() {
    val begin = System.currentTimeMillis()
    runBlocking {
        calculateMatrix()
    }

    println("finish: ${System.currentTimeMillis() - begin} ms")
}


suspend fun calculateMatrix() {
    delay(200)

    // 1 coroutine
    Matrix.random(1, 10_000) timesConcurrent  Matrix.random(10_000, 1_000)
}
