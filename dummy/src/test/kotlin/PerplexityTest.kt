import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.mockk.*
import okhttp3.*
import okhttp3.Call
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException

class PerplexityTest {

    private val apiKey = "test-api-key"
    private val objectMapper: ObjectMapper = ObjectMapper().apply {
        registerKotlinModule()
    }

    @Test
    fun `query should return a list of users on success`() {
        val mockClient = mockk<OkHttpClient>()
        val mockCall = mockk<Call>()
        val mockResponse = mockk<Response>()
        val mockResponseBody = mockk<ResponseBody>()

        val perplexity = Perplexity(apiKey, mockClient)

        val mockApiResponse = """
            {
                "choices": [
                    {
                        "message": {
                            "content": "[{\"name\":\"PlayerOne\",\"age\":25,\"level\":76,\"score\":18423,\"achievements\":[\"First Kill\",\"Treasure Hunter\"],\"active\":true}]"
                        }
                    }
                ]
            }
        """
        every { mockClient.newCall(any()) } returns mockCall
        every { mockCall.execute() } returns mockResponse
        every { mockResponseBody.string() } returns mockApiResponse
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body } returns mockResponseBody
        justRun { mockResponse.close() }

        val users = perplexity.query()

        assertEquals(1, users.size, "The user list should contain one item.")
        val user = users[0]
        assertEquals("PlayerOne", user.name)
        assertEquals(25, user.age)
        assertEquals(76, user.level)
        assertEquals(18423, user.score)
        assertTrue(user.active)
        assertEquals(listOf("First Kill", "Treasure Hunter"), user.achievements)

        verify(exactly = 1) { mockClient.newCall(any()) }
        verify(exactly = 1) { mockCall.execute() }
        verify(exactly = 1) { mockResponse.body }
        verify(exactly = 1) { mockResponseBody.string() }
    }

    @Test
    fun `query should throw IOException when response is not successful`() {
        val mockResponse = mockk<Response>()

        every { mockResponse.isSuccessful } returns false
        every { mockResponse.code } returns 404
        every { mockResponse.body.string() } returns "Not Found"
        justRun { mockResponse.close() }

        val mockClient = mockk<OkHttpClient>()
        every { mockClient.newCall(any()).execute() } returns mockResponse

        val perplexity = Perplexity(apiKey, mockClient)

        val exception = assertThrows<IOException> {
            perplexity.query()
        }
        assertEquals("Unexpected code 404", exception.message)

        verify(exactly = 1) { mockResponse.body }
        verify { mockResponse.close() }
    }
}