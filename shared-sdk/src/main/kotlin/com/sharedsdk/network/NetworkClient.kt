package com.sharedsdk.network

interface NetworkClient {

    fun postData(url: String, data: String): Result<String>
}