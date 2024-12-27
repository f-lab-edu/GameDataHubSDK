
package com.sharedsdk.serialization

interface JsonSerializer<T> {
    fun serialize(data: T): String
    fun deserialize(jsonData: String): T
}