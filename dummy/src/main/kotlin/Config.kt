import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import java.io.File


object Config {
    private val fileDir = File(javaClass.classLoader.getResource("config.yml")!!.file)
    var aiApiKey = ""

    fun initialize() {
        val objectMapper = ObjectMapper(YAMLFactory())
        val config = objectMapper.readValue(fileDir, ConfigData::class.java)

        aiApiKey = config.aiApiKey
    }
}

private class ConfigData {
    val aiApiKey: String = ""
}