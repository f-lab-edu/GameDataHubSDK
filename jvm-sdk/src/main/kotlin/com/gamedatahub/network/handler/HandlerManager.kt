package com.gamedatahub.network.handler

class HandlerManager(
    private vararg val handlers: FailureHandler
) : ErrorHandler {
    override fun handle(error: Throwable) {
        invokeHandlers(handlers.iterator(), error)
    }

    private fun invokeHandlers(iterator: Iterator<FailureHandler>, error: Throwable) {
        if (iterator.hasNext()) {
            val currentHandler = iterator.next()
            currentHandler.handle(error) {
                invokeHandlers(iterator, error)
            }
        }
    }
}