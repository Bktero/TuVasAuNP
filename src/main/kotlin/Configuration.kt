import io.github.cdimascio.dotenv.dotenv
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile

private fun getEnv(name: String): String {
    val value = System.getenv(name)
    requireNotNull(value) { "The env variable $name must defined" }
    require(value.isNotEmpty()) { "The env variable $name must not be empty" }
    return value
}

data class Configuration(
    val token: String,
    val adminId: String,
    val userIds: List<String>,
) {
    companion object {
        private val logger = KotlinLogging.logger {}

        fun getFromEnv(): Configuration {
            val envFile = Path(".env")

            if (envFile.exists() && envFile.isRegularFile()) {
                logger.info { "Using file $envFile" }
                val dotenv = dotenv()
                return Configuration(
                    dotenv["TU_VAS_AU_NP_BOT_TOKEN"],
                    dotenv["TU_VAS_AU_NP_BOT_ADMIN_ID"],
                    dotenv["TU_VAS_AU_NP_BOT_USER_IDS"].split(';').toList(),
                )
            } else {
                return Configuration(
                    getEnv("TU_VAS_AU_NP_BOT_TOKEN"),
                    getEnv("TU_VAS_AU_NP_BOT_ADMIN_ID"),
                    getEnv("TU_VAS_AU_NP_BOT_USER_IDS").split(';').toList(),
                )
            }
        }
    }
}
