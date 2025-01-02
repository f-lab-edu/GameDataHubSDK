
package com.gamedatahub.datacollection

import com.gamedatahub.serialization.JsonSerializer
import com.gamedatahub.network.NetworkClient
import com.gamedatahub.network.handler.ErrorHandler
import com.gamedatahub.network.handler.RetryableError


class DataCollector<T>(
    private val networkClient: NetworkClient,
    private val jsonSerializer: JsonSerializer<T>,
    private val handlerManager: ErrorHandler
) {

    fun collect(serverUrl: String, data: T) {
        val jsonData = serializeToJson(data)

        try {
            networkClient.postDataAsync(serverUrl, jsonData)
        } catch (e: Exception) {
            handlerManager.handle(
                RetryableError(
                    message = e.message,
                    retryCount = 0,
                    retryAction = { collect(serverUrl, data) }
                )
            )
        }
    }

    private fun serializeToJson(data: T): String {
        return jsonSerializer.serialize(data)
    }
}