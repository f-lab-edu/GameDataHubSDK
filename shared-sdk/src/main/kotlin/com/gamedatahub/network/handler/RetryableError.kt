package com.gamedatahub.network.handler

class RetryableError(
    override val message: String?,
    val retryCount: Int = 0,
    val retryAction: suspend () -> Unit
) : Throwable(message)