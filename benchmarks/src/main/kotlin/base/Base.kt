package base

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
@State(Scope.Thread)
abstract class Base {

    @Param("NO_PROBES", "DEFAULT", "CREATION_ST", "SANITIZE_ST", "C_S")
    open lateinit var mode: Modes


    @Setup(Level.Iteration)
    open fun setup() = Unit

    @TearDown(Level.Iteration)
    open fun tearDown() = Unit

    abstract fun run(blackhole: Blackhole)

    @Benchmark
    @Fork(1)
    fun runBenchmark(blackhole: Blackhole) {
        run(blackhole)
    }

}

/**
 * NO_PROBES - mode with disabled DebugProbes
 * DEFAULT - mode with enabled only DebugProbes
 * CREATION_ST (C) - mode with enabled DebugProbes and enabled creation stack traces
 * SANITIZE_ST (S) - mode with enabled DebugProbes and enabled sanitizing stack traces
 * LAZY_CREATION_ST (L) - mode with enabled DebugProbes and enabled lazy creation stack traces. **Only for patched lib!**
 * C_S = CREATION_ST + SANITIZE_ST
 * C_L = CREATION_ST + LAZY_CREATION_ST
 * S_L = SANITIZE_ST + LAZY_CREATION_ST
 * C_S_L = CREATION_ST + SANITIZE_ST + LAZY_CREATION_ST
 */
enum class Modes(val value: Int) {
    NO_PROBES(0),
    DEFAULT(1),
    SANITIZE_ST(2 or DEFAULT.value),
    CREATION_ST(4 or DEFAULT.value),
    LAZY_CREATION_ST(8 or DEFAULT.value),
    C_S(CREATION_ST.value or SANITIZE_ST.value),
    C_L(CREATION_ST.value or LAZY_CREATION_ST.value),
    S_L(SANITIZE_ST.value or LAZY_CREATION_ST.value),
    C_S_L(CREATION_ST.value or SANITIZE_ST.value or LAZY_CREATION_ST.value);

    val installedProbes: Boolean
        get() = value.isSet(0)

    val sanitizeStackTraces: Boolean
        get() = value.isSet(1)

    val creationStackTrace: Boolean
        get() = value.isSet(2)

    val lazyCreationStackTraces: Boolean
        get() = value.isSet(3)
}

fun Int.isSet(bit: Int): Boolean = (this shr bit) and 1 == 1