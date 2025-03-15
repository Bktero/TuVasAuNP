import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.io.File

class MyBot(private val telegramClient: TelegramClient) : LongPollingSingleThreadUpdateConsumer {


    override fun consume(update: Update?) {
        update!!
        println(update)

        if (!update.hasMessage()) {
            println("The update has no message")
            return
        }

        val message = update.message;

        if (message.hasText()) {
            println("Text = $message")
        }

        if (message.hasPhoto()) {
            println("Photo = ${message.photo}")
            for (photo in message.photo) {
                // We seem to receive the same image in 4 different sizes
                println(photo)
                if (photo.filePath != null) {
                    print(photo.filePath)
                    val downloaded = telegramClient.downloadFile(photo.filePath)
                    println(downloaded)
                    val copied =
                        downloaded.copyTo(File("/home/pierre/Documents/MyFirstTelegramBot/Download/${photo.filePath}"))
                    print(copied)
                    downloaded.delete()
                } else {
                    println("No file path to save the photo")
                    val method = GetFile(photo.fileId)
                    val file = telegramClient.execute(method)
                    println(file)
                    val downloaded = telegramClient.downloadFile(file.filePath)
                    println(downloaded)
                    val copied =
                        downloaded.copyTo(File("/home/pierre/Documents/MyFirstTelegramBot/Download/${photo.fileId}"))
                    print(copied)
                    downloaded.delete()
                }
            }
        }

        if (message.hasDocument()) {
            println("Document = ${message.document}")
        }
    }
}
