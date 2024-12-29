
package com.gamedatahub.datacollection

import com.gamedatahub.serialization.JsonSerializer
import com.gamedatahub.network.NetworkClient


class DataCollector<T>(
    private val networkClient: NetworkClient,
    private val jsonSerializer: JsonSerializer<T>
) {

    fun collect(serverUrl: String, data: T): Result<String> {
        val jsonData = serializeToJson(data)
        return networkClient.postData(serverUrl, jsonData)
    }

    private fun serializeToJson(data: T): String {
        return jsonSerializer.serialize(data)
    }
}