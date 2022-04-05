package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.sampling.writers.CborDumpWriter
import kotlinx.coroutines.profiler.sampling.writers.Compression
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

        val dumpsDirectory = File(args?.getOrNull(1) ?: "out/results/profile")
//        val dumpWriter = JsonDumpWriter(dumpsDirectory)
        val dumpWriter = CborDumpWriter(dumpsDirectory, Compression.GZIP)
        CoroutinesProfiler(dumpWriter).attachAndRun(5)
    }

}
