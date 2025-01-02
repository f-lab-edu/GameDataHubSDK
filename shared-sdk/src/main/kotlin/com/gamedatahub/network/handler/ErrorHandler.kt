package com.gamedatahub.network.handler

interface ErrorHandler {

    fun handle(error: Throwable)
}