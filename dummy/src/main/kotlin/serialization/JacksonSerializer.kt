package serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.gamedatahub.serialization.JsonSerializer


class JacksonSerializer<T>(
    private val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule()),
    private val clazz: Class<T>
) : JsonSerializer<T> {

    override fun serialize(data: T): String {
        return objectMapper.writeValueAsString(data)
    }

    override fun deserialize(jsonData: String): T {
        return objectMapper.readValue(jsonData, clazz)
    }
}