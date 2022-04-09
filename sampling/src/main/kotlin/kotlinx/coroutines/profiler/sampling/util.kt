package kotlinx.coroutines.profiler.sampling

import java.nio.ByteBuffer

fun ByteArray.toInt(): Int = ByteBuffer.wrap(this).int
fun Int.toByteArray(): ByteArray = ByteBuffer.allocate(Int.SIZE_BYTES).putInt(this).array()