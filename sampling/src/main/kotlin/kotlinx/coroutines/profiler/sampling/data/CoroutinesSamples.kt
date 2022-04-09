package kotlinx.coroutines.profiler.sampling.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineProbe
import kotlinx.coroutines.profiler.sampling.toInt
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream

@JvmInline
@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
value class CoroutinesProbes(
    val samples: List<ProfilingCoroutineProbe>
) {
    companion object {
        fun readFromStream(input: InputStream): CoroutinesProbes = CoroutinesProbes(buildList {
            while (input.available() != 0) {
                val sampleSize = input.readNBytes(Int.SIZE_BYTES).toInt()
                val sample = ProtoBuf.decodeFromByteArray<ProfilingCoroutineProbe>(input.readNBytes(sampleSize))
                add(sample)
            }
        })
    }
}