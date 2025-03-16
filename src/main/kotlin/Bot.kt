import data.Ride
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate

class Bot(private val telegramClient: TelegramClient, private val userId: String) :
    LongPollingSingleThreadUpdateConsumer {

    private val downloadDirectory = File("working/downloads")
    private val mapDownloader = MapDownloader(downloadDirectory)

    init {
        downloadDirectory.deleteRecursively()
    }

    override fun consume(update: Update?) {
        update!!
        println(update)

        if (canAccept(update)) {
            when (update.message.text) {

                "/start" -> {
                    // No action
                }

                "/ride", "ride", "r", "R" -> {
                    ride()
                }

                else -> {
                    sendText("Sorry, I don't know what to say...")
                }
            }
        }
    }

    private fun canAccept(update: Update): Boolean {
        if (update.hasMessage()) {
            if (update.message.isUserMessage) {
                if (update.message.from.id.toString() == userId) {
                    // Don't ask me why all APIs expect strings but internally, ID for Chat and User are longs
                    // This is why there is a toString() here
                    return true
                } else {
                    println("This message is not from our user")
                }
            } else {
                println("This message is not from a user")
            }
        } else {
            println("Update has no message")
        }

        return false
    }

    private fun sendText(text: String) {
        assert(text.isNotEmpty()) { "Cannot send an empty text" }
        val method = SendMessage.builder()
            .chatId(userId) // we send only to our user
            .text(text)
            .build()

        telegramClient.execute(method)
    }

    private fun sendFile(file: File, caption: String) {
        val method = SendDocument.builder()
            .chatId(userId) // we send only to our user
            .document(InputFile(file))
            .caption(caption)
            .build()

        telegramClient.execute(method)
    }

    //------------------------------------------------------------------------------------------------------------------
    // The 'ride' command

    private fun ride() {
        val today = LocalDate.now()

        when (today.dayOfWeek) {
            DayOfWeek.TUESDAY -> {
                sendText("It's Tuesday, the ride is tomorrow. Maybe the maps are already available, let me check.")
                val tomorrow = today.plusDays(1)
                processDate(tomorrow)
            }

            DayOfWeek.WEDNESDAY -> {
                sendText("It's ride day! Let me get the maps for you!")
                processDate(today)
            }

            else -> {
                sendText("Sorry, it's only ${today.dayOfWeek}... You have to wait a little more for the next ride.")
            }
        }
    }

    private fun processDate(date: LocalDate) {
        val rides = RideDownloader().day(date)

        when (rides.size) {
            0 -> {
                sendText("I'm sorry but there is not ride today...")
            }

            1 -> {
                processRide(rides[0])
            }

            else -> {
                sendText("There are several (${rides.size}) rides today, I don't know what to do.")
            }
        }
    }

    private fun processRide(ride: Ride) {
        val desiredGroups = setOf(WednesdayGroups.C3, WednesdayGroups.SuperChillGravel)

        sendText("Getting the maps of groups $desiredGroups of ride ${ride.title}")

        for (group in ride.groups) {
            val guessedGroup = WednesdayGroups.guess(group.name)
            if (guessedGroup in desiredGroups) {
                val file = mapDownloader.download(group.map)
                println("Map for group ${group.name} is here: $file")
                val caption = "This is the map for ${group.name}. Meeting time is ${group.meetingTime}"
                sendFile(file, caption)
            }
        }
    }
}