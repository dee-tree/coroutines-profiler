package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.repackaged.net.bytebuddy.ByteBuddy
import kotlinx.coroutines.repackaged.net.bytebuddy.agent.ByteBuddyAgent
import kotlinx.coroutines.repackaged.net.bytebuddy.dynamic.loading.ClassReloadingStrategy
import java.lang.instrument.Instrumentation

object Agent {

    @ExperimentalCoroutinesApi
    @JvmStatic
    fun premain(args: String?, instrumentation: Instrumentation) {
        val begin = System.currentTimeMillis()
        val mainThread = Thread.currentThread()
        println("agent: thread: ${mainThread}")
        println("agent: PID: " + ProcessHandle.current().pid())

        Profiler.attachAndRun(5)
        println("Agent worked ${System.currentTimeMillis() - begin} ms")
    }

    private fun redefine() {
        ByteBuddyAgent.install()
        val cl = Class.forName("kotlin.coroutines.jvm.internal.DebugProbesKt")
        val cl2 = Class.forName("com.sokolov.java_agent.agent.DebugProbesKt")

        ByteBuddy()
            .redefine(cl2)
            .name(cl.name)
            .make()
            .load(cl.classLoader, ClassReloadingStrategy.fromInstalledAgent())
    }

}