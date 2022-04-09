package kotlinx.coroutines.profiler.sampling.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo
import kotlinx.coroutines.profiler.sampling.toInt
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream

@JvmInline
@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
value class CoroutinesStructure(
    val structure: List<ProfilingCoroutineInfo>
) {
    companion object {
        fun readFromStream(input: InputStream): CoroutinesStructure = CoroutinesStructure(buildList {
            while (input.available() != 0) {
                val coroutineInfoSize = input.readNBytes(Int.SIZE_BYTES).toInt()
                val sample = ProtoBuf.decodeFromByteArray<ProfilingCoroutineInfo>(input.readNBytes(coroutineInfoSize))
                add(sample)
            }
        })
    }
}