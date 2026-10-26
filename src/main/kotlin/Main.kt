import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import kotlin.io.path.Path

val logger = KotlinLogging.logger {}

fun main() {
    logger.info { "Starting TuVasAuNP..." }

    val configuration = Configuration.getFromEnv()
    val telegramClient = OkHttpTelegramClient(configuration.token)

    // Download the latest maps
    //
    // It takes a few seconds to get it from NP's server.
    // The first /ride or /rattrapage commands in the bot, or the first request to our web server
    // won't pay the delay of downloading by having in cache.
    val finder = SuperchillGravelFinder(Path("working/downloads"))
    finder.findPrevious()
    finder.findNext()

    // Run the webserver
    val server = WebServer(finder, port = 8080, baseUrl = configuration.baseUrl)
    server.start()

    // Run the bot
    runTelegramBot(telegramClient, configuration, finder)

    // Notify admin that the application is up
    val message = SendMessage(configuration.adminId, "Back dans les bacs!")
    try {
        telegramClient.execute(message)
    } catch (e: TelegramApiException) {
        e.printStackTrace()
        logger.warn {
            """
            Cannot send initial message: ${e.message}
            The userId is likely to be bad or the user hasn't started the bot yet.
            Are you connected to the Internet?
            """.trimIndent()
        }
        println()
    }
}

private fun runTelegramBot(
    telegramClient: OkHttpTelegramClient,
    configuration: Configuration,
    finder: SuperchillGravelFinder,
) {
    val bot = Bot(telegramClient, configuration.adminId, configuration.userIds, finder)
    val app = TelegramBotsLongPollingApplication()

    try {
        app.registerBot(configuration.token, bot)
    } catch (e: TelegramApiException) {
        e.printStackTrace()
        logger.error {
            """
            "
            Cannot start telegram bot: ${e.message}
            Are you connected to the Internet?
            """.trimIndent()
        }
    }
}
