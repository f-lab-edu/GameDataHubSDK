package com.gamedatahub.network

interface NetworkClient {

    fun postDataAsync(url: String, data: String)
}