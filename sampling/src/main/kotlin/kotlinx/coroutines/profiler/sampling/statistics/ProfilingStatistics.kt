package kotlinx.coroutines.profiler.sampling.statistics

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.sampling.data.CoroutinesProbes
import kotlinx.coroutines.profiler.sampling.data.CoroutinesStructure
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File
import java.util.zip.GZIPInputStream


@ExperimentalSerializationApi
@ExperimentalCoroutinesApi
@kotlinx.serialization.Serializable
data class ProfilingStatistics(
    val structureFilePath: String,
    val probesFilePath: String,
    val coroutinesCount: Int,
    val probesCount: Int,
    val specifiedProbesIntervalMillis: Int,
    val internalStatistics: ProfilingInternalStatistics? = null
) {
    val structureFile: File
        get() = File(structureFilePath)

    val probesFile: File
        get() = File(probesFilePath)

    companion object {
        fun fromFile(file: File): ProfilingStatistics {
            return Json.decodeFromStream(file.inputStream())
        }
    }

    fun writeToFile(file: File) {
        Json.encodeToStream(this, file.outputStream())
    }

    fun loadStructureFromFile(): CoroutinesStructure {
        return CoroutinesStructure.readFromStream(GZIPInputStream(structureFile.inputStream()))
    }

    fun loadProbesFromFile(): CoroutinesProbes {
        return CoroutinesProbes.readFromStream(GZIPInputStream(probesFile.inputStream()))
    }
}

