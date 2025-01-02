import com.gamedatahub.network.JvmNetworkClient
import com.gamedatahub.datacollection.DataCollector
import com.gamedatahub.network.handler.ErrorHandler
import com.gamedatahub.network.handler.HandlerManager
import com.gamedatahub.serialization.JsonSerializer
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import serialization.JacksonSerializer

class MainIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var networkClient: JvmNetworkClient
    private lateinit var jsonSerializer: JsonSerializer<User>
    private lateinit var dataCollector: DataCollector<User>

    private val maxRetries = 3

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val handlerManager = HandlerManager()
        networkClient = JvmNetworkClient()

        jsonSerializer = JacksonSerializer(clazz = User::class.java)
        dataCollector = DataCollector(networkClient, jsonSerializer, handlerManager)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `성공적인 네트워크 전송`() {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{ \"status\": \"success\" }")
        mockWebServer.enqueue(mockResponse)

        val dummyUser = User(
            name = "Test User",
            age = 25,
            level = 5,
            score = 1000,
            achievements = listOf("Achievement1", "Achievement2"),
            active = true
        )
        val serverUrl = mockWebServer.url("/collect").toString()

        dataCollector.collect(serverUrl, dummyUser)

        val request = mockWebServer.takeRequest()
        assertEquals("/collect", request.path, "요청 경로가 올바르지 않습니다.")
        assertEquals("POST", request.method, "HTTP 메서드가 올바르지 않습니다.")
        assertTrue(request.body.readUtf8().contains("Test User"), "전송된 데이터가 올바르지 않습니다.")
    }

    @Test
    fun `네트워크 실패 시 재시도 및 최종 실패 처리`() {
        repeat(maxRetries) {
            val mockResponse = MockResponse()
                .setResponseCode(500)
                .setBody("{ \"status\": \"error\" }")
            mockWebServer.enqueue(mockResponse)
        }

        val dummyUser = User(
            name = "Retry User",
            age = 30,
            level = 10,
            score = 2000,
            achievements = listOf("AchievementX", "AchievementY"),
            active = false
        )
        val serverUrl = mockWebServer.url("/retry").toString()

        dataCollector.collect(serverUrl, dummyUser)

        repeat(maxRetries) {
            val request = mockWebServer.takeRequest()
            assertEquals("/retry", request.path, "요청 경로가 올바르지 않습니다.")
            assertEquals("POST", request.method, "HTTP 메서드가 올바르지 않습니다.")
            assertTrue(request.body.readUtf8().contains("Retry User"), "재시도 시 전송된 데이터가 올바르지 않습니다.")
        }
    }

    @Test
    fun `재시도 후 성공`() {
        // 첫 두 번은 실패, 세 번째는 성공
        repeat(2) {
            val mockResponse = MockResponse()
                .setResponseCode(500) // 실패 응답
                .setBody("{ \"status\": \"error\" }")
            mockWebServer.enqueue(mockResponse)
        }
        val mockSuccessResponse = MockResponse()
            .setResponseCode(200) // 성공 응답
            .setBody("{ \"status\": \"success\" }")
        mockWebServer.enqueue(mockSuccessResponse)

        val dummyUser = User(
            name = "Retry Success User",
            age = 35,
            level = 15,
            score = 5000,
            achievements = listOf("AchievementA", "AchievementB"),
            active = true
        )
        val serverUrl = mockWebServer.url("/success").toString()

        dataCollector.collect(serverUrl, dummyUser)

        // 두 번 실패 요청
        repeat(2) { index ->
            val request = mockWebServer.takeRequest()
            assertEquals("/success", request.path, "요청 경로가 올바르지 않습니다. (실패 요청 ${index + 1})")
            assertEquals("POST", request.method, "HTTP 메서드가 올바르지 않습니다. (실패 요청 ${index + 1})")
            assertTrue(request.body.readUtf8().contains("Retry Success User"), "실패 요청 시 전송된 데이터가 올바르지 않습니다.")
        }

        // 세 번째 요청은 성공
        val successRequest = mockWebServer.takeRequest()
        assertEquals("/success", successRequest.path, "요청 경로가 올바르지 않습니다. (성공 요청)")
        assertEquals("POST", successRequest.method, "HTTP 메서드가 올바르지 않습니다. (성공 요청)")
        assertTrue(successRequest.body.readUtf8().contains("Retry Success User"), "성공 요청 시 전송된 데이터가 올바르지 않습니다.")
    }
}