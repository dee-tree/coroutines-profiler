package kotlinx.coroutines.profiler.app

import TimeMeasureTest
import kotlinx.coroutines.profiler.app.matrix.Matrix
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*


class MatrixTests : TimeMeasureTest() {

    @Tag(MEASURE_AS_REPEATED)
    @RepeatedTest(10)
    fun measureTimeFewCoroutinesFewTime(): Unit = runBlocking {
        Matrix.random(5, 10_000) timesConcurrent  Matrix.random(10_000, 1_000)
    }

    @Tag(MEASURE_AS_REPEATED)
    @RepeatedTest(10)
    fun measureTimeManyCoroutinesFewTime(): Unit = runBlocking {
        Matrix.random(50, 1_000) timesConcurrent  Matrix.random(1_000, 1_000)
    }

    @Tag(MEASURE_AS_REPEATED)
    @RepeatedTest(100)
    fun measureTimeManyCoroutinesBalanced(): Unit = runBlocking {
        Matrix.random(1_000, 1_000) timesConcurrent  Matrix.random(1_000, 50)
    }

    @Tag(MEASURE_AS_REPEATED)
    @RepeatedTest(10)
    fun measureTimeFewCoroutinesBalanced(): Unit = runBlocking {
        Matrix.random(50, 1_000) timesConcurrent  Matrix.random(1_000, 1_000)
    }
}