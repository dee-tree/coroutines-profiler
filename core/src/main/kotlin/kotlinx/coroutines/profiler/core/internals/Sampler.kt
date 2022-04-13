package kotlinx.coroutines.profiler.core.internals

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.debug.CoroutineInfo
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.debug.State
import kotlinx.coroutines.profiler.core.data.CoroutineProbe
import kotlinx.coroutines.profiler.core.data.CoroutinesDump
import kotlinx.coroutines.profiler.core.data.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.core.data.statistics.ProbeHandlingStatistics
import kotlinx.coroutines.profiler.core.data.statistics.ProbeTakingStatistics

@ExperimentalCoroutinesApi
abstract class Sampler {

    protected var probeId: Int = -1

    abstract fun probe(onNewCoroutineFound: (ProfilingCoroutineInfo) -> Unit): CoroutinesDump

    abstract fun getHandlingStatistics(): ProbeHandlingStatistics?
    abstract fun getTakingStatistics(): ProbeTakingStatistics?

    /**
     * Be careful! Calling #dumpCoroutinesInfo() updates probeId
     */
    protected fun dumpCoroutinesInfo(): CoroutinesInfoDump =
        CoroutinesInfoDump(++probeId, DebugProbes.dumpCoroutinesInfo())


    private val monitoringCoroutinesId = mutableSetOf<Long>()

    @ExperimentalCoroutinesApi
    protected open fun CoroutinesInfoDump.transform(
        onNewCoroutineFound: (ProfilingCoroutineInfo) -> Unit
    ): CoroutinesDump {
        val profilingDump = dump.map { coroutine ->
            if (coroutine.id !in monitoringCoroutinesId) {
                onNewCoroutineFound(coroutine.toProfilingCoroutineInfo(this))
            }

            coroutine.toProbe(probeId)
        }

        val coroutinesIdThisDump = dump.map { it.id }
        monitoringCoroutinesId.removeAll(monitoringCoroutinesId.minus(coroutinesIdThisDump.toSet()))
        monitoringCoroutinesId.addAll(coroutinesIdThisDump)

        return CoroutinesDump(probeId, profilingDump)
    }

}

fun CoroutineInfo.toProbe(probeId: Int) = CoroutineProbe(
    probeId,
    id,
    state.toCommonState(),
    lastObservedThread?.name,
    lastObservedStackTrace().map { it.toString() }
)

private fun State.toCommonState() = when (this) {
    State.CREATED -> kotlinx.coroutines.profiler.core.data.State.CREATED
    State.RUNNING -> kotlinx.coroutines.profiler.core.data.State.RUNNING
    State.SUSPENDED -> kotlinx.coroutines.profiler.core.data.State.SUSPENDED
}


@ExperimentalCoroutinesApi
data class CoroutinesInfoDump(
    val probeId: Int,
    val dump: List<CoroutineInfo>
)


@ExperimentalCoroutinesApi
private fun CoroutineInfo.toProfilingCoroutineInfo(withDump: CoroutinesInfoDump): ProfilingCoroutineInfo =
    ProfilingCoroutineInfo(
        id,
        job?.let { it::class.simpleName } ?: "unknown",
        findParent(withDump)?.id,
        creationStackTrace.map { it.toString() },
    )

@ExperimentalCoroutinesApi
private fun CoroutineInfo.findParent(dump: CoroutinesInfoDump): CoroutineInfo? {
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