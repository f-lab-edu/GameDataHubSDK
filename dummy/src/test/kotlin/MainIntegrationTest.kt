import com.gamedatahub.network.JvmNetworkClient
import com.gamedatahub.datacollection.DataCollector
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

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        networkClient = JvmNetworkClient()
        jsonSerializer = JacksonSerializer(clazz = User::class.java)
        dataCollector = DataCollector(networkClient, jsonSerializer)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `통합 테스트 - 데이터 수집 및 네트워크 전송`() {
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
}