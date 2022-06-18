package ktor

import base.ProbeTakingWithDelayedTracesBenchmark
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.coroutineScope


@Suppress("unused")
open class ProbeTakingWithDelayedTracesKtor :
    ProbeTakingWithDelayedTracesBenchmark() {

//    private lateinit var server: ApplicationEngine
//
//    override fun doOnIterationSetup() {
//        server = embeddedServer(Netty, 9091) {
//            routing {
//                get("/greeting") {
//                    call.respond("Hello!")
//                }
//            }
//        }.start(false)
//    }
//
//    override fun doOnIterationTearDown() {
//        server.stop(1, 1)
//    }

    override suspend fun doInCoroutineScope() {
        coroutineScope {
            embeddedServer(Netty, 9091) {
                routing {
                    get("/greeting") {
                        call.respond("Hello!")
                    }
                }
            }.start(true)

        }
    }

}