package kotlinx.coroutines.profiler.visual

import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.sampling.ProfilingResults
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import java.io.File
import java.util.zip.GZIPInputStream


@Suppress("EXPERIMENTAL_API_USAGE")
fun main(args: Array<String>) {
    println("args: $args")
    // arg: coroutines dumps file
    val coroutinesDumpsFile = File(args[0])

    val profilingResults = ProfilingResults.readFromFile(coroutinesDumpsFile)

    val rootCoroutines = ProfilingCoroutineInfo.fromDump(profilingResults.structure, profilingResults.samples)

    showCommonCoroutinesTable(rootCoroutines)

    showCoroutineTable(rootCoroutines.first())

    rootCoroutines.forEach {
        showCoroutineStatesRanges(it)
    }

    generateHtmlContent(rootCoroutines, File(args[0]).parentFile)
}
