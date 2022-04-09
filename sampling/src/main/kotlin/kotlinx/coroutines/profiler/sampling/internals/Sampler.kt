package kotlinx.coroutines.profiler.sampling.internals

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.debug.CoroutineInfo
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineProbe
import kotlinx.coroutines.profiler.sampling.statistics.ProfilingInternalStatistics
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalCoroutinesApi
abstract class Sampler {

    protected var probeId: Int = -1

    abstract fun probe(onNewCoroutineFound: (ProfilingCoroutineInfo) -> Unit): ProfilingCoroutineDump

    @ExperimentalSerializationApi
    abstract fun getInternalStatistics(): ProfilingInternalStatistics.Builder?

    /**
     * Be careful! Calling #dumpCoroutinesInfo() updates probeId
     */
    protected fun dumpCoroutinesInfo(): CoroutineInfoDump =
        CoroutineInfoDump(++probeId, DebugProbes.dumpCoroutinesInfo())


    private val monitoringCoroutinesId = mutableSetOf<Long>()

    @ExperimentalCoroutinesApi
    protected open fun CoroutineInfoDump.transform(
        onNewCoroutineFound: (ProfilingCoroutineInfo) -> Unit
    ): ProfilingCoroutineDump {
        val profilingDump = dump.map { coroutine ->
            if (coroutine.id !in monitoringCoroutinesId) {
                onNewCoroutineFound(coroutine.toProfilingCoroutineInfo(this))
            }

            ProfilingCoroutineProbe.fromCoroutineInfo(coroutine, probeId)
        }

        val coroutinesIdThisDump = dump.map { it.id }
        monitoringCoroutinesId.removeAll(monitoringCoroutinesId.minus(coroutinesIdThisDump.toSet()))
        monitoringCoroutinesId.addAll(coroutinesIdThisDump)

        return ProfilingCoroutineDump(probeId, profilingDump)
    }

}


@ExperimentalCoroutinesApi
data class CoroutineInfoDump(
    val probeId: Int,
    val dump: List<CoroutineInfo>
)

@ExperimentalCoroutinesApi
data class ProfilingCoroutineDump(
    val probeId: Int,
    val dump: List<ProfilingCoroutineProbe>
)

@ExperimentalCoroutinesApi
private fun CoroutineInfo.toProfilingCoroutineInfo(withDump: CoroutineInfoDump): ProfilingCoroutineInfo =
    ProfilingCoroutineInfo(
        id,
        findParent(withDump)?.id,
        creationStackTrace.map { it.toString() },
        job?.let { it::class.simpleName }
    )

@ExperimentalCoroutinesApi
private fun CoroutineInfo.findParent(dump: CoroutineInfoDump): CoroutineInfo? {
    dump.dump.forEach { supposedParent ->
        supposedParent.job?.findInChildren(false) { supposedParentJob ->
            supposedParentJob == this.job
        }?.let { _ -> return supposedParent }
    }
    return null
}

private fun Job.findInChildren(considerThis: Boolean, condition: (Job) -> Boolean): Job? {
    if (considerThis && condition(this)) return this
    children.toList().forEach {
        it.findInChildren(true, condition)?.let { found -> return found }
    }
    return null
}