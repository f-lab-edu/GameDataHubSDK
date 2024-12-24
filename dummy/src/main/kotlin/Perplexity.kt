import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.gson.Gson
import java.io.IOException
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull

private const val prompt = "You are a helpful assistant."

class Perplexity(
    private val apiKey: String,
    private val client: OkHttpClient = OkHttpClient()
) {
    private val requestUrl = "https://api.perplexity.ai/chat/completions"
    private val gson = Gson()
    private val model = "llama-3.1-sonar-small-128k-online"
    private val objectMapper = ObjectMapper()
        .registerKotlinModule()
        .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)


    fun query(): List<User> {
        val requestBody = createRequestBody()

        val request = Request.Builder()
            .url(requestUrl)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                println("Error Response: ${response.body?.string()}")
                throw IOException("Unexpected code $response")
            }
            val responseBody = response.body?.string() ?: throw IOException("Empty response body")
            val apiResponse = objectMapper.readValue(responseBody, ApiResponse::class.java)

            val content = apiResponse.choices.firstOrNull()?.message?.content ?: "[]"
            val removeSuffix = content.trim().removePrefix("```json").removeSuffix("```")
            return objectMapper.readValue(removeSuffix, objectMapper.typeFactory.constructCollectionType(List::class.java, User::class.java))
        }
    }

    private fun createRequestBody(): String {
        return gson.toJson(
            mapOf(
                "model" to model,
                "messages" to listOf(
                    mapOf(
                        "role" to "system",
                        "content" to prompt
                    ),
                    mapOf(
                        "role" to "user",
                        "content" to """
                             Generate realistic game user dummy data in JSON format. 
                            Each user should include the following attributes: 
                            - `name`: a unique username
                            - `age`: an age between 10 and 50
                            - `level`: a game progress level (1 to 100)
                            - `score`: a score value between 1000 and 50000
                            - `achievements`: random list of 1-5 achievement titles
                            - `active`: boolean indicating whether the user is currently active
                        """.trimIndent()
                    ),
                    mapOf(
                        "role" to "assistant",
                        "content" to """
                            {
                                "name": "PlayerOne",
                                "age": 25,
                                "level": 76,
                                "score": 18423,
                                "achievements": ["First Kill", "Treasure Hunter"],
                                "active": true
                            }
                        """.trimIndent()
                    ),
                    mapOf(
                        "role" to "user",
                        "content" to """
                            Please generate another example of realistic game user data with randomized values. 
                            Provide a total of 10 unique examples in JSON format without any additional explanation or text, 
                            and ensure there are no enclosing backticks, special symbols, or unnecessary characters wrapping the content.
                        """.trimIndent()
                    )
                )
            )
        )
    }
}

data class User(
    val name: String,
    val age: Int,
    val level: Int,
    val score: Int,
    val achievements: List<String>,
    val active: Boolean
)

data class ApiResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

data class Message(
    val content: String
)