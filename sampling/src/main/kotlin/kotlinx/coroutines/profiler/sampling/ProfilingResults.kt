package kotlinx.coroutines.profiler.sampling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.zip.GZIPInputStream

@ExperimentalCoroutinesApi
@kotlinx.serialization.Serializable
data class ProfilingResults(
    val structure: List<ProfilingCoroutineInfo>,
    val samples: List<ProfilingCoroutineSample>
) {
    @ExperimentalSerializationApi
    companion object {
        fun writeToFile(
            file: File,
            structureFilePath: String,
            samplesFilePath: String,
            coroutinesCount: Int,
            samplesCount: Int,
            samplingIntervalMillis: Long
        ) {
            ProfilingResultsFile(
                structureFilePath,
                samplesFilePath,
                coroutinesCount,
                samplesCount,
                samplingIntervalMillis
            ).writeToFile(file)
        }

        fun readFromFile(file: File): ProfilingResults {
            val resultsFile = ProfilingResultsFile.fromFile(file)

            val samplesFile = resultsFile.samplesFile
            val structureFile = resultsFile.structureFile

            val structure = readStructureFromStream(GZIPInputStream(structureFile.inputStream()))
            val samples = readSamplesFromStream(GZIPInputStream(samplesFile.inputStream()))

            return ProfilingResults(
                structure,
                samples
            )
        }

        fun readSamplesFromStream(input: InputStream): List<ProfilingCoroutineSample> = buildList {
            while (input.available() != 0) {
                val sampleSize = input.readNBytes(Int.SIZE_BYTES).toInt()
                val sample = ProtoBuf.decodeFromByteArray<ProfilingCoroutineSample>(input.readNBytes(sampleSize))
                add(sample)
            }
        }

        fun readStructureFromStream(input: InputStream): List<ProfilingCoroutineInfo> = buildList {
            while (input.available() != 0) {
                val coroutineInfoSize = input.readNBytes(Int.SIZE_BYTES).toInt()
                val sample = ProtoBuf.decodeFromByteArray<ProfilingCoroutineInfo>(input.readNBytes(coroutineInfoSize))
                add(sample)
            }
        }
    }
}

@ExperimentalSerializationApi
@kotlinx.serialization.Serializable
data class ProfilingResultsFile(
    val structureFilePath: String,
    val samplesFilePath: String,
    val coroutinesCount: Int,
    val samplesCount: Int,
    val samplesIntervalMillis: Long
) {
    val structureFile: File
        get() = File(structureFilePath)

    val samplesFile: File
        get() = File(samplesFilePath)

    companion object {
        fun fromFile(file: File): ProfilingResultsFile {
            return Json.decodeFromStream(file.inputStream())
        }
    }

    fun writeToFile(file: File) {
        Json.encodeToStream(this, file.outputStream())
    }
}

private fun ByteArray.toInt(): Int = ByteBuffer.wrap(this).int