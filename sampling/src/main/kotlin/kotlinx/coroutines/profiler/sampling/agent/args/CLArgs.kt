package kotlinx.coroutines.profiler.sampling.agent.args

import java.io.File

interface CLArgs {
    val outputDirectory: File
    val probesIntervalMillis: Int
    val collectInternalStatistics: Boolean
}

const val DEFAULT_PROBES_INTERVAL = 5
val DEFAULT_OUTPUT_DIRECTORY = File(System.getProperty("user.home"))
const val DEFAULT_SHOULD_INTERNAL_STATISTICS_BE_COLLECTED = false


interface CLArgsParser {
    fun parseArgs(args: Array<String>, actionWithArgs: CLArgs.() -> Unit)
    fun parseArgs(args: String, actionWithArgs: CLArgs.() -> Unit)
}