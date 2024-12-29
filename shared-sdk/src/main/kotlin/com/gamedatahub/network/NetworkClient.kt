package com.gamedatahub.network

interface NetworkClient {

    fun postData(url: String, data: String): Result<String>
}