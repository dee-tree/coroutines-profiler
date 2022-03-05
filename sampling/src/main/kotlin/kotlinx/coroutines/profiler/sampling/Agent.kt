package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File
import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.Instrumentation
import java.security.ProtectionDomain

object Agent {

    @ExperimentalCoroutinesApi
    @JvmStatic
    fun premain(args: String?, instrumentation: Instrumentation) {
        instrumentation.addTransformer(object : ClassFileTransformer{
            override fun transform(
                loader: ClassLoader?,
                className: String?,
                classBeingRedefined: Class<*>?,
                protectionDomain: ProtectionDomain?,
                classfileBuffer: ByteArray?
            ): ByteArray {
                if (className?.contains("debug") == true) {
                    println("CLASS: ${className}")
                }
                if (className == "kotlinx/coroutines/debug/internal/DebugProbesImplKt")
                    println("HELLO FUCK YOU!!!")
                return super.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer)
            }
        })
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