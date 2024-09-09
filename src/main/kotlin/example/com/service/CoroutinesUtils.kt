package example.com.service

import com.google.api.core.ApiFuture
import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Adapter to convert an ApiFuture API in a suspendable function
 */
suspend fun <T> ApiFuture<T>.await(dispatcher: CoroutineDispatcher = Dispatchers.Default): T =
    suspendCancellableCoroutine { cancellableContinuation ->
        val callback = object : ApiFutureCallback<T> {
            override fun onFailure(t: Throwable) = cancellableContinuation.resumeWithException(t)
            override fun onSuccess(result: T) = cancellableContinuation.resume(result)
        }
        ApiFutures.addCallback(this, callback, dispatcher.asExecutor())
    }