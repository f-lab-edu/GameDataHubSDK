import java.util.*



fun main() {
    Config.initialize()
    
    val query = Perplexity(Config.aiApiKey)
        .query()

    println(query)
}
