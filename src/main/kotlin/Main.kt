import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

fun main() {
    val configuration = Configuration.getFromEnv()
    val telegramClient = OkHttpTelegramClient(configuration.token)

    // Send initial message
    val message = SendMessage(configuration.adminId, "Back dans les bacs!")
    try {
        telegramClient.execute(message)
    } catch (e: TelegramApiException) {
        e.printStackTrace()
        println("The userId is likely to be bad or the user hasn't started the bot yet")
    }

    // Run the bot
    val app = TelegramBotsLongPollingApplication()
    val bot = Bot(telegramClient, configuration.adminId, configuration.userIds)
    app.registerBot(configuration.token, bot)
}
