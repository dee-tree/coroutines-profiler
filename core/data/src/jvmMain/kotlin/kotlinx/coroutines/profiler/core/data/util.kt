package kotlinx.coroutines.profiler.core.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.zip.GZIPInputStream

fun readProfilingResultsFile(file: File) = ProfilingResultFile.fromString(file.readText())

fun ProfilingResultFile.writeToFile(file: File) {
    file.writeText(Json.encodeToString(this))
}

val ProfilingResultFile.structureFile: File
    get() = File(structureFilePath)

val ProfilingResultFile.probesFile: File
    get() = File(probesFilePath)

fun ProfilingResultFile.loadStructure(): LinearCoroutinesStructure =
    readCoroutinesStructureFromStream(GZIPInputStream(structureFile.inputStream()))

fun ProfilingResultFile.loadProbes(): Probes =
    readProbesFromStream(GZIPInputStream(probesFile.inputStream()))


/**
 * Remember that coroutines structure is stored in GZIP'ed protobuf format
 */
@ExperimentalSerializationApi
internal fun readCoroutinesStructureFromStream(input: InputStream): LinearCoroutinesStructure =
    LinearCoroutinesStructure(buildList {
        while (input.available() != 0) {
            val coroutineInfoSize = input.readNBytes(Int.SIZE_BYTES).toInt()
            val coroutineInfo = ProtoBuf.decodeFromByteArray<ProfilingCoroutineInfo>(input.readNBytes(coroutineInfoSize))
            add(coroutineInfo)
        }
    })

/**
 * Remember that probes is stored in GZIP'ed protobuf format
 */
@ExperimentalSerializationApi
internal fun readProbesFromStream(input: InputStream): Probes = Probes(buildList {
    while (input.available() != 0) {
        val probeSize = input.readNBytes(Int.SIZE_BYTES).toInt()
        val probe = ProtoBuf.decodeFromByteArray<CoroutineProbe>(input.readNBytes(probeSize))
        add(probe)
    }
})

fun ProfilingCoroutineInfo.encodeToByteArray(): ByteArray {

    val encodedInfo = ProtoBuf.encodeToByteArray(this)
    val encodedSize = encodedInfo.size.toByteArray()

    return encodedSize + encodedInfo
}

fun CoroutinesDump.encodeToByteArray(): ByteArray {

    val bytes = mutableListOf<ByteArray>()

    dump.forEach { probe ->
        val encodedProbe = ProtoBuf.encodeToByteArray(probe)

        bytes.add(encodedProbe.size.toByteArray())
        bytes.add(encodedProbe)
    }

    val byteArray = ByteArray(bytes.fold(0) { acc, current -> acc + current.size })

    var writeIdx = 0
    bytes.forEach { bytes ->
        bytes.forEach {
            byteArray[writeIdx] = it
            writeIdx++
        }
    }

    return byteArray
}


fun ByteArray.toInt(): Int = ByteBuffer.wrap(this).int
fun Int.toByteArray(): ByteArray = ByteBuffer.allocate(Int.SIZE_BYTES).putInt(this).array()