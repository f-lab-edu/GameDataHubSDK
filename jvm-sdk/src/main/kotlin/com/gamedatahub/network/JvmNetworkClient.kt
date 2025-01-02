package com.gamedatahub.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class JvmNetworkClient(
    private val client: OkHttpClient = OkHttpClient()
) : NetworkClient {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun postDataAsync(url: String, data: String) {
        scope.launch {
            try {
                val response = client.makePostRequest(url, data)
                TODO("성공 핸들링")
            } catch (e: Exception) {
                TODO("실패 핸들링")
            }
        }
    }
}

private suspend fun OkHttpClient.makePostRequest(url: String, data: String): String {
    val requestBody = data.toRequestBody("application/json".toMediaType())
    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    return suspendCancellableCoroutine { continuation ->
        val call = this.newCall(request)

        call.enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    continuation.resume(response.body?.string() ?: "")
                } else {
                    continuation.resumeWithException(Exception("Error: ${response.code} - ${response.message}"))
                }
                response.close()
            }

            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }
        })

        continuation.invokeOnCancellation {
            call.cancel()
        }
    }
}