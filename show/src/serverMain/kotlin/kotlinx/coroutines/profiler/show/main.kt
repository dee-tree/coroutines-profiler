@file:Suppress("EXPERIMENTAL_API_USAGE")
package kotlinx.coroutines.profiler.show

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.sampling.ProfilingResults
import kotlinx.coroutines.profiler.sampling.ProfilingResultsFile
import java.io.File

private lateinit var profilingResults: ProfilingResultsFile

fun main(args: Array<String>) {
    // arg is path to profiling result file
    embeddedServer(Netty, 9090, watchPaths = listOf("classes", "resources")) {
        install(ContentNegotiation) {
            json()
        }

        routing {
            get("/") {
                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }
//            static("/") {
//                resources("")
//            }

            get("/profilingInfo") {
                loadProfilingResults(File(args[0]))
                call.respond(profilingResults.extractInfo())
            }

            get("/stacks") {
                val results = ProfilingResults.readFromFile(File(args[0]))

                val rootCoroutines = ProfilingCoroutineInfo.fromDump(results.structure, results.samples)

//                val flameJson = File("coro-stacks.json")

                val sampleFrame = rootCoroutines.toSampleFrame()
//                call.respond(SampleFrame("KEK", 200, stacktrace = listOf(), samples = 200))
                call.respond(sampleFrame/*.copy(children = null)*/)
//                call.respondText(rootCoroutines.toSampleFrame(), ContentType.Application.Json)
//                val rootCoroutines = ProfilingCoroutineInfo.fromDump(profilingResults.structure, profilingResults.samples)
            }

        }
    }.start(wait = true)
}

private fun loadProfilingResults(file: File): ProfilingResultsFile {
    profilingResults = ProfilingResultsFile.fromFile(file)
    return profilingResults
}