package kotlinx.coroutines.profiler.app.matrix

import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext
import kotlin.random.Random

class Matrix(val rows: Int, val columns: Int, private vararg val numbers: Double) {

    constructor(rowsCount: Int, columnsCount: Int, rows: List<DoubleArray>) :
            this(rowsCount, columnsCount, *rows.reduce { acc, doubles -> acc.plus(doubles) })


    operator fun get(row: Int, column: Int): Double = numbers[row * columns + column]

    operator fun get(row: Int): DoubleArray {
        return numbers.sliceArray(row * columns until (row + 1) * columns)
    }

    fun column(idx: Int): DoubleArray = numbers.filterIndexed { index, _ ->
        (index - idx) % columns == 0
    }.toDoubleArray()


    operator fun times(b: Matrix): Matrix {
        require(columns == b.rows)

        val result: Matrix

        val rows = buildList {
            repeat(this@Matrix.rows) { row ->
                add(times(row, b))
            }
        }

        result = Matrix(this@Matrix.rows, b.columns, rows)
        return result
    }


    // parallel with coroutines
    suspend infix fun timesConcurrent(b: Matrix): Matrix {
        require(columns == b.rows)

        val result: Matrix

        coroutineContext
        withContext(Dispatchers.Default) {
            val rows = (0 until rows).map { row ->
                async {
                    timesConcurrent(row, b)
                }
            }.awaitAll()

            result = Matrix(this@Matrix.rows, b.columns, rows)
        }
        return result
    }

    private fun times(aRow: Int, b: Matrix): DoubleArray =
        buildList(this@Matrix.rows) {
            repeat(b.columns) { column ->
                add(timesForRow(aRow, b, column))
            }
        }.toDoubleArray()


    /**
     * returns row aRow for result matrix
     */
    private suspend fun timesConcurrent(aRow: Int, b: Matrix): DoubleArray = withContext(Dispatchers.Default) {
        buildList(this@Matrix.rows) {
            repeat(b.columns) { column ->
                add(timesForRow(aRow, b, column))
            }
        }.toDoubleArray()
    }


    private fun timesForRow(aRow: Int, b: Matrix, bColumn: Int): Double {
        return buildList {
            repeat(this@Matrix.columns) { curIdx ->
                add(this@Matrix[aRow, curIdx] * b[curIdx, bColumn])
            }
        }.sum()
    }


    override fun toString(): String {
        val maxCharsPerElement = numbers.maxOf { it.toString().length }
        return buildString {
            for (row in 0 until rows) {
                append(

                    this@Matrix[row].joinToString(separator = " ") {
                        it.toString().padEnd(maxCharsPerElement)
                    }
                )
                appendLine()
            }
        }
    }

    companion object {

        @JvmStatic
        fun random(rows: Int, columns: Int): Matrix {
            val numbers = buildList {
                repeat(rows * columns) {
                    add(Random.nextDouble())
                }
            }.toDoubleArray()

            return Matrix(rows, columns, *numbers)
        }

        @JvmStatic
        fun random(rows: IntRange, columns: IntRange): Matrix = random(rows.random(), columns.random())
    }
}