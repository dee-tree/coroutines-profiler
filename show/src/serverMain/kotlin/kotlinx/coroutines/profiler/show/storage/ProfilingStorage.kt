@file:Suppress("EXPERIMENTAL_API_USAGE")

package kotlinx.coroutines.profiler.show.storage


import kotlinx.coroutines.profiler.core.data.LinearCoroutinesStructure
import kotlinx.coroutines.profiler.core.data.Probes
import kotlinx.coroutines.profiler.core.data.ProfilingResultFile
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
    fun isCoroutinesProbesInitialized(): Boolean = _coroutinesProbes != null
    fun setCoroutinesProbes(probes: Probes?) {
        _coroutinesProbes = probes
    }

    private var _coroutinesProbes: Probes? = null
    val coroutinesProbes: Probes
        get() = _coroutinesProbes!!


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


}

