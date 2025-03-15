private fun getEnv(name: String): String {
    val value = System.getenv(name)
    requireNotNull(value) { "The env variable $name must defined" };
    require(value.isNotEmpty()) { "The env variable $name must not be empty" }
    return value
}

data class Configuration(val token: String, val userId: String) {
    companion object {
        fun getFromEnv(): Configuration {
            return Configuration(
                getEnv("MY_FIRST_TELEGRAM_BOT_TOKEN"),
                getEnv("MY_FIRST_TELEGRAM_BOT_USER_ID")
            )
        }
    }
}
