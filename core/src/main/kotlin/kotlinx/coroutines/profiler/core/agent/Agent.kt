package kotlinx.coroutines.profiler.core.agent

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.core.CoroutinesProfiler
import kotlinx.coroutines.profiler.core.agent.args.impl.CLArgsParserImpl
import kotlinx.coroutines.profiler.core.writers.Compression
import kotlinx.coroutines.profiler.core.writers.ProtobufDumpWriter
import java.lang.instrument.Instrumentation
import kotlin.contracts.ExperimentalContracts
import kotlin.time.ExperimentalTime

object Agent {
    @ExperimentalContracts
    @ExperimentalTime
    @ExperimentalCoroutinesApi
    @JvmStatic
    fun premain(args: String?, instrumentation: Instrumentation) {
        println("Agent started...")
        CLArgsParserImpl().parseArgs(args ?: "") {
            println("Configuration:")
            println("\t*\tOutput directory: $outputDirectory")
            println("\t*\tProbes interval: $probesIntervalMillis ms")
            println("\t*\tCollect internals: $collectInternalStatistics")


            val dumpWriter = ProtobufDumpWriter(outputDirectory, Compression.GZIP, probesIntervalMillis)
            CoroutinesProfiler(dumpWriter, collectInternalStatistics).attachAndRun(probesIntervalMillis)
        }
    }

}
