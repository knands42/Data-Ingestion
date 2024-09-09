package example.com.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun <T> Flow<T>.chunked(size: Int): Flow<List<T>> = flow {
    val elements = ArrayList<T>(size)
    collect {
        elements.add(it)
        if (elements.size == size) {
            emit(elements.toList())
            elements.clear()
        }
    }
    if (elements.isNotEmpty()) {
        emit(elements.toList())
    }
}

/*
*   The size of the Channel is given by the parallelism parameter.
*/
suspend fun <T> Flow<T>.parallelProcessing(
    parallelism: Int = 20,
    processElement: suspend (T) -> Unit
) {
    // Max RAM overhead = memory_size_of(T) * parallelism * 2
    withContext(Dispatchers.Default) {
        val inputChannel = Channel<T>(parallelism)
        launch {
            collect {
                inputChannel.send(it)
            }
            inputChannel.close()
        }
        for (i in 0..<parallelism) launch {
            for (element in inputChannel) {
                processElement(element)
            }
        }
    }
}