package com.gamedatahub.network

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

data class NetworkClientConfig(
    var isRetryEnabled: Boolean = true,
    var maxRetries: Int = 2,
    var retryDelayMillis: Long = 1000,
    var backoffFactor: Double = 2.0
)

class JvmNetworkClient private constructor(
    private val client: OkHttpClient,
    val config: NetworkClientConfig,
) : NetworkClient {

    override fun postDataAsync(url: String, data: String) {
        client.makePostRequest(url, data)
    }

    private fun OkHttpClient.makePostRequest(url: String, data: String) {
        val requestBody = data.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        this.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("HTTP: ${response.code} - ${response.message}")
            }
        }
    }

    class Builder {
        private var client: OkHttpClient = OkHttpClient()
        private var config: NetworkClientConfig = NetworkClientConfig()

        private val yamlMapper: ObjectMapper = ObjectMapper(YAMLFactory())
            .registerModule(KotlinModule())

        fun loadFromYaml(filePath: String = "config.yml") =
            apply {
                val file = File(filePath)
                config = if (!file.exists()) {
                    NetworkClientConfig()
                } else {
                    yamlMapper.readValue(file, NetworkClientConfig::class.java)
                }
            }

        fun httpClient(client: OkHttpClient) = apply { this.client = client }
        fun enableRetry(isEnabled: Boolean) = apply { this.config = this.config.copy(isRetryEnabled = isEnabled) }
        fun maxRetries(maxRetries: Int) = apply { this.config = this.config.copy(maxRetries = maxRetries) }
        fun retryDelayMillis(delayMillis: Long) = apply { this.config = this.config.copy(retryDelayMillis = delayMillis) }
        fun backoffFactor(factor: Double) = apply { this.config = this.config.copy(backoffFactor = factor) }

        fun build(): JvmNetworkClient {
            return JvmNetworkClient(client, config)
        }
    }
}