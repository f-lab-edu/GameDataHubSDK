package com.jvm.sdk.network

import com.sharedsdk.network.NetworkClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.Serial


class JvmNetworkClient(
    private val client: OkHttpClient = OkHttpClient()
) : NetworkClient {

    override fun postData(url: String, data: String): Result<String> {
        return try {
            val requestBody = RequestBody.create("application/json".toMediaType(), data) // JSON 데이터 요청
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Result.success(response.body?.string() ?: "Success")
                } else {
                    Result.failure(Exception("Error: ${response.code} - ${response.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e) // 예외 처리
        }
    }
}