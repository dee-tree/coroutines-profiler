package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File
import java.lang.instrument.Instrumentation

object Agent {

    @ExperimentalCoroutinesApi
    @JvmStatic
    fun premain(args: String?, instrumentation: Instrumentation) {
        val args = args?.split("\\s")
        println("agent args: ${args}")
        // first arg: create subdir with dumps time. Can be true or false
        // second arg: directory for dumps. String

        val mainThread = Thread.currentThread()
        println("agent: run at thread: ${mainThread}")
        println("agent: PID: " + ProcessHandle.current().pid())

        val dumpsDirectory = File(args?.getOrNull(1) ?: "/Users/Dmitry.Sokolov/ideaProjects/coroutines-profiler/sampling/out/dumps/notSpecified")
        CoroutinesProfiler(JsonDumpWriter(dumpsDirectory, args?.getOrNull(0).toBoolean())).attachAndRun(5)
    }

}
