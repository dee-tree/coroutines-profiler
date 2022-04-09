package kotlinx.coroutines.profiler.sampling.agent.args.impl

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import kotlinx.coroutines.profiler.sampling.agent.args.*
import java.io.File

class CLArgsParserImpl internal constructor(): CLArgsParser {

    override fun parseArgs(args: Array<String>, actionWithArgs: CLArgs.() -> Unit) {
        actionWithArgs(ArgParser(args).parseInto(CLArgsParserImpl::CLArgsImpl))
    }

    override fun parseArgs(args: String, actionWithArgs: CLArgs.() -> Unit) {
        return parseArgs(args.trim().split(regex = "\\s".toRegex()).toTypedArray(), actionWithArgs)
    }

    class CLArgsImpl(parser: ArgParser) : CLArgs {
        override val outputDirectory: File by parser.storing<File>("-o", "--output",
        help="Path to output file", transform={ File(this) }).default(DEFAULT_OUTPUT_DIRECTORY)
        override val probesIntervalMillis by parser.storing<Int>("-i", "--interval",
        help="Probes interval in milliseconds", transform={ this.toInt() }).default(DEFAULT_PROBES_INTERVAL)
        override val collectInternalStatistics by parser.flagging("-s", "--internal", "--stat",
        help="Should internal statistics such as mean probe time collected")
            .default(DEFAULT_SHOULD_INTERNAL_STATISTICS_BE_COLLECTED)
    }
}