@file:Suppress("EXPERIMENTAL_API_USAGE")

package kotlinx.coroutines.profiler.show.storage


import io.ktor.application.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.profiler.core.data.*
import kotlinx.coroutines.profiler.core.data.ProfilingCoroutineInfo.Companion.addProbes
import kotlinx.coroutines.profiler.show.routes.checkProfilingResultsFileExists
import java.io.File


object ProfilingStorage {

    // Linear coroutines structure
    fun isLinearCoroutinesStructureInitialized(): Boolean = _linearCoroutinesStructure != null
    fun setLinearCoroutinesStructure(structure: LinearCoroutinesStructure?) {
        _linearCoroutinesStructure = structure
    }

    private var _linearCoroutinesStructure: LinearCoroutinesStructure? = null
    val linearCoroutinesStructure: LinearCoroutinesStructure
        get() = _linearCoroutinesStructure!!


    // Coroutine probes
    fun isCoroutinesProbesRangesInitialized(): Boolean = _coroutinesProbesRanges != null

    fun setCoroutinesProbesRanges(probes: List<CoroutineProbesRange>?) {
        _coroutinesProbesRanges = probes
    }

    private var _coroutinesProbesRanges: List<CoroutineProbesRange>? = null
    val coroutinesProbesRanges: List<CoroutineProbesRange>
        get() = _coroutinesProbesRanges!!


    // Profiling results
    fun isProfilingResultsInitialized(): Boolean = _profilingResults != null
    fun setProfilingResults(results: ProfilingResultFile?) {
        _profilingResults = results
    }

    private var _profilingResults: ProfilingResultFile? = null
    val profilingResults: ProfilingResultFile
        get() = _profilingResults!!


    // Profiling results file
    fun isProfilingResultsFileInitialized() = _profilingResultFile != null
    fun setProfilingResultsFile(file: File?) {
        _profilingResultFile = file
    }

    private var _profilingResultFile: File? = null
    val profilingResultFile: File
        get() = _profilingResultFile!!


    suspend fun PipelineContext<Unit, ApplicationCall>.initializeProfilingResultsIfNot() {
        if (_profilingResults != null) return
        initializeProfilingResults()
    }

    suspend fun PipelineContext<Unit, ApplicationCall>.initializeProfilingResults() {
        checkProfilingResultsFileExists()
        setProfilingResults(readProfilingResultsFile(profilingResultFile))
    }

    suspend fun PipelineContext<Unit, ApplicationCall>.initializeCoroutinesIfNot() {
        if (_linearCoroutinesStructure != null) return
        if (_coroutinesProbesRanges != null) return

        initializeProfilingResultsIfNot()

        setCoroutinesProbesRanges(profilingResults.loadProbesRanges())
        setLinearCoroutinesStructure(
            profilingResults.loadStructure().addProbes(coroutinesProbesRanges)
        )
    }

    suspend fun PipelineContext<Unit, ApplicationCall>.initializeCoroutines() {
        initializeProfilingResults()

        setCoroutinesProbesRanges(profilingResults.loadProbesRanges())
        setLinearCoroutinesStructure(
            profilingResults.loadStructure().addProbes(coroutinesProbesRanges)
        )
    }
}

