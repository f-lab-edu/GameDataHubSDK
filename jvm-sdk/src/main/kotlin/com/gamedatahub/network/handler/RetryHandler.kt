package com.gamedatahub.network.handler

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.math.pow

class RetryHandler(
    private val clientScope: CoroutineContext,
    private val maxRetries: Int = 3,
    private val initialDelayMillis: Long = 1000L
) : FailureHandler {

    override fun handle(error: Throwable, next: () -> Unit) {
        val retryError = error as? RetryableError
            ?: throw IllegalArgumentException("RetryableError instance is required!")

        val retryCount = retryError.retryCount

        if (retryCount < maxRetries) {
            println("Retrying... (${retryCount + 1}/$maxRetries)")

            CoroutineScope(clientScope).launch {
                val delayMillis = initialDelayMillis * (2.0.pow(retryCount.toDouble())).toLong()
                delay(delayMillis)

                try {
                    println("Executing retry attempt ${retryCount + 1}")
                    retryError.retryAction()
                } catch (e: Exception) {
                    println("Retry attempt ${retryCount + 1} failed: ${e.message}")
                    handle(
                        RetryableError(
                            message = e.message,
                            retryCount = retryCount + 1,
                            retryAction = retryError.retryAction
                        ),
                        next
                    )
                }
            }
        } else {
            println("Retries exhausted. Passing to next handler.")
            next()
        }
    }
}