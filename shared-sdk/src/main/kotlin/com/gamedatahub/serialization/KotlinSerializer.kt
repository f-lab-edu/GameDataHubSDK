package com.gamedatahub.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class KotlinSerializer<T>(
    private val serializer: KSerializer<T>
) : JsonSerializer<T> {

    private val json = Json {
        encodeDefaults = true
        prettyPrint = true
    }

    override fun serialize(data: T): String {
        return json.encodeToString(serializer, data)
    }

    override fun deserialize(jsonData: String): T {
        return json.decodeFromString(serializer, jsonData)
    }
}