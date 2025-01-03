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
import java.io.File

class MainIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var networkClient: JvmNetworkClient
    private lateinit var jsonSerializer: JsonSerializer<User>
    private lateinit var dataCollector: DataCollector<User>

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        networkClient = JvmNetworkClient.Builder().build()
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

    @Test
    fun `config yml 파일 설정이 제대로 적용된다`() {
        val expectedEnabled = true
        val expectedRetries = 3
        val expectedDelayMillis = 2000L

        val networkYml = """
        isRetryEnabled: $expectedEnabled
        maxRetries: $expectedRetries
        retryDelayMillis: $expectedDelayMillis
        backoffFactor: 1.5
    """.trimIndent()

        val configFile = File("network.yml")
        configFile.writeText(networkYml)

        val client = JvmNetworkClient.Builder()
            .loadFromYaml("network.yml")
            .build()

        val config = client.config

        assertEquals(expectedEnabled, config.isRetryEnabled, "재시도 설정이 올바르게 설정되지 않았습니다.")
        assertEquals(expectedRetries, config.maxRetries, "재시도 횟수가 올바르게 설정되지 않았습니다.")
        assertEquals(expectedDelayMillis, config.retryDelayMillis, "재시도 딜레이 값이 올바르게 설정되지 않았습니다.")

        configFile.delete()
    }
}