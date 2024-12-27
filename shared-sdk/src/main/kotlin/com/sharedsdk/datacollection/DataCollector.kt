
package com.sharedsdk.datacollection

import com.sharedsdk.serialization.JsonSerializer
import com.sharedsdk.network.NetworkClient
import com.sharedsdk.serialization.KotlinSerializer
import kotlinx.serialization.Serializable


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