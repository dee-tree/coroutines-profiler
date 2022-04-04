package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.debug.CoroutineInfo


private val monitoringCoroutinesId = mutableSetOf<Long>()

@ExperimentalCoroutinesApi
fun transform(
    dump: List<CoroutineInfo>,
    dumpId: Long,
    onNewCoroutineFound: (ProfilingCoroutineInfo) -> Unit
): List<ProfilingCoroutineSample> {
    val samples = dump.map { coroutine ->
        if (coroutine.id !in monitoringCoroutinesId) {
            onNewCoroutineFound(ProfilingCoroutineInfo(
                coroutine.id,
                coroutine.findParent(dump)?.id,
                coroutine.creationStackTrace.map { it.toString() },
                coroutine.job?.let { it::class.simpleName }
            ))
        }

        ProfilingCoroutineSample.fromCoroutineInfo(coroutine, dumpId)
    }

    val coroutinesIdThisDump = dump.map { it.id }
    monitoringCoroutinesId.removeAll(monitoringCoroutinesId.minus(coroutinesIdThisDump.toSet()))
    monitoringCoroutinesId.addAll(coroutinesIdThisDump)

    return samples
}

@ExperimentalCoroutinesApi
internal fun CoroutineInfo.findParent(dump: List<CoroutineInfo>): CoroutineInfo? {
    dump.forEach { supposedParent ->
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