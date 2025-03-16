private fun getEnv(name: String): String {
    val value = System.getenv(name)
    requireNotNull(value) { "The env variable $name must defined" }
    require(value.isNotEmpty()) { "The env variable $name must not be empty" }
    return value
}

data class Configuration(val token: String, val userId: String) {
    companion object {
        fun getFromEnv(): Configuration {
            return Configuration(
                getEnv("TU_VAS_AU_NP_BOT_TOKEN"),
                getEnv("TU_VAS_AU_NP_BOT_USER_ID")
            )
        }
    }
}
