package kotlinx.coroutines.profiler.core.data

import kotlinx.coroutines.profiler.core.data.statistics.ProfilingStatistics
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@kotlinx.serialization.Serializable
data class ProfilingResultFile(
    val structureFilePath: String,
    val probesFilePath: String,

    val profilingStatistics: ProfilingStatistics
) {

    companion object {
        fun fromString(text: String) = Json.decodeFromString<ProfilingResultFile>(text)
    }
}
