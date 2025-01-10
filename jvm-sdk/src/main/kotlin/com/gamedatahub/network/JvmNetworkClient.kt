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
    val isRetryEnabled: Boolean = false,
    val maxRetries: Int = 1,
    val retryDelayMillis: Long = 1000,
    val backoffFactor: Double = 2.0
)

class JvmNetworkClient private constructor(
    private val client: OkHttpClient,
    val config: NetworkClientConfig,
) : NetworkClient {

    override fun postDataAsync(url: String, data: String) {
        client.makePostRequestAsync(url, data)
    }

    private fun OkHttpClient.makePostRequestAsync(
        url: String,
        data: String,
        attempt: Int = 0
    ) {
        val requestBody = data.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val maxAttempts = config.maxRetries
        var delay = config.retryDelayMillis

        this.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                var tmpAttempt = attempt
                tmpAttempt++
                if (tmpAttempt <= maxAttempts && config.isRetryEnabled) {
                    delay = (delay * config.backoffFactor).toLong()
                    makePostRequestAsync(url, data, tmpAttempt)
                } else {
                    println("Request failed after ${tmpAttempt} attempts: ${e.message}")
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful)
                        onFailure(call, IOException("HTTP ${response.code}: ${response.message}"))
                }
            }
        })
    }

    class Builder {
        private var client: OkHttpClient = OkHttpClient()
        private var config: NetworkClientConfig = NetworkClientConfig()

        private val yamlMapper: ObjectMapper by lazy {
            ObjectMapper(YAMLFactory()).registerModule(KotlinModule())
        }

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