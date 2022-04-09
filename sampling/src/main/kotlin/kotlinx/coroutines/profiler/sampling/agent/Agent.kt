package kotlinx.coroutines.profiler.sampling.agent

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.sampling.CoroutinesProfiler
import kotlinx.coroutines.profiler.sampling.agent.args.impl.CLArgsParserImpl
import kotlinx.coroutines.profiler.sampling.internals.SamplerImpl
import kotlinx.coroutines.profiler.sampling.internals.StatsCollectingSamplerImpl
import kotlinx.coroutines.profiler.sampling.writers.Compression
import kotlinx.coroutines.profiler.sampling.writers.ProtobufDumpWriter
import java.lang.instrument.Instrumentation

object Agent {
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
            val sampler = if (collectInternalStatistics) StatsCollectingSamplerImpl() else SamplerImpl()
            CoroutinesProfiler(dumpWriter, sampler, collectInternalStatistics).attachAndRun(probesIntervalMillis)
        }
    }

}
