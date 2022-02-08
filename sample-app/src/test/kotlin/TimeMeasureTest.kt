import org.junit.jupiter.api.*
import java.lang.reflect.Method
import java.util.Optional


open class TimeMeasureTest {

    private var begin: Long = 0

    @BeforeEach
    fun before(testInfo: TestInfo) {
        if (testInfo.tags.contains(MEASURE_AS_REPEATED)) {
            when {
                currentMethod == null -> {
                    currentMethod = testInfo.testMethod
                    repeatedTestsCount = 1
                }
                currentMethod == testInfo.testMethod -> {
                    repeatedTestsCount++
                }
                else -> {
                    // new repeated test case
                    printRepeatedTestsInfo()

                    summaryRepeatedTestTimeExecution = 0
                    currentMethod = testInfo.testMethod
                    repeatedTestsCount = 1
                }
            }
        }

        begin = System.nanoTime()
    }

    @AfterEach
    fun after(testInfo: TestInfo) {
        val executionTime = System.nanoTime() - begin
        summaryTimeExecution += executionTime
        testsCount++

        if (testInfo.tags.contains(MEASURE_AS_REPEATED)) {
            summaryRepeatedTestTimeExecution += executionTime
        }

        println("Test  ${testInfo.testMethod.get()}: ${testInfo.displayName} executed ${(executionTime) / 1_000_000} ms")
    }

    companion object {
        const val MEASURE_AS_REPEATED = "Measure-as-repeated"

        private var testsCount = 0
        private var repeatedTestsCount = 0
        private var summaryTimeExecution: Long = 0
        private var summaryRepeatedTestTimeExecution: Long = 0

        private var currentMethod: Optional<Method>? = null
        @JvmStatic
        @AfterAll
        fun afterAll() {
            if (repeatedTestsCount != 0)
                printRepeatedTestsInfo()

            println()
            println("All tests executed ${summaryTimeExecution / 1_000_000} ms")
            println("Mean time of ${testsCount} tests executions: ${(summaryTimeExecution / testsCount)/ 1_000_000} ms")
            println()

        }

        fun printRepeatedTestsInfo() {
            println()
            println("All repeated ${currentMethod?.get()} executed totally ${summaryRepeatedTestTimeExecution / 1_000_000} ms")
            println("Mean time of ${repeatedTestsCount} repeated ${currentMethod?.get()} is ${(summaryRepeatedTestTimeExecution / repeatedTestsCount) / 1_000_000} ms")
            println()
        }
    }
}