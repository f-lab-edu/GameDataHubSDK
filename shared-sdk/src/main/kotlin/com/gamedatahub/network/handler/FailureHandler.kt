package com.gamedatahub.network.handler

interface FailureHandler {
    fun handle(error: Throwable, next: () -> Unit)
}