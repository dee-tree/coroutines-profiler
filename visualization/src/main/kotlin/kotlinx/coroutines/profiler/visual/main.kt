package kotlinx.coroutines.profiler.visual

import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineSample
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File


@Suppress("EXPERIMENTAL_API_USAGE")
fun main(args: Array<String>) {
    // first: coroutines list dump file
    // second: samples dump file

    println("first: ${args[0]}")
    println("second: ${args[1]}")

    val coroutinesListDumpFile = File(args[0])
    val samplesDumpFile = File(args[1])


    val coroutinesList = Json.decodeFromString<List<ProfilingCoroutineInfo>>(coroutinesListDumpFile.readText())
    val samples = Json.decodeFromString<List<ProfilingCoroutineSample>>(samplesDumpFile.readText())

    val rootCoroutines = ProfilingCoroutineInfo.fromDump(coroutinesList, samples)

    showCommonCoroutinesTable(rootCoroutines)

    showCoroutineTable(rootCoroutines.first())

    rootCoroutines.forEach {
        showCoroutineStatesRanges(it)
    }

}