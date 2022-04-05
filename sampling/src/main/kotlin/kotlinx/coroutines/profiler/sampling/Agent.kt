package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.sampling.writers.Compression
import kotlinx.coroutines.profiler.sampling.writers.ProtobufDumpWriter
import java.io.File
import java.lang.instrument.Instrumentation

object Agent {
    @ExperimentalCoroutinesApi
    @JvmStatic
    fun premain(args: String?, instrumentation: Instrumentation) {
        val args = args?.split("\\s")
        println("agent args: ${args}")
        // arg: directory for dumps: String

        println("agent runs on PID ${ProcessHandle.current().pid()}")

        val samplingInterval = 5L

        val dumpsDirectory = File(args?.getOrNull(1) ?: "out/results/profile")
        val dumpWriter = ProtobufDumpWriter(dumpsDirectory, Compression.GZIP, samplingInterval)
        CoroutinesProfiler(dumpWriter).attachAndRun(samplingInterval)
    }

}
