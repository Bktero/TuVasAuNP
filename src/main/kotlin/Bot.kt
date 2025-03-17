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
import java.time.LocalDateTime
import java.time.Period

private fun DayOfWeek.toFrench(): String {
    when (this) {
        DayOfWeek.MONDAY -> {
            return "lundi"
        }

        DayOfWeek.TUESDAY -> {
            return "mardi"
        }

        DayOfWeek.WEDNESDAY -> {
            return "mercredi"
        }

        DayOfWeek.THURSDAY -> {
            return "jeudi"
        }

        DayOfWeek.FRIDAY -> {
            return "vendredi"
        }

        DayOfWeek.SATURDAY -> {
            return "samedi"
        }

        DayOfWeek.SUNDAY -> {
            return "dimanche"
        }
    }
}

class Bot(private val telegramClient: TelegramClient, private val adminId: String, private val userIds: List<String>) :
    LongPollingSingleThreadUpdateConsumer {

    private val downloadDirectory = File("working/downloads")
    private val mapDownloader = MapDownloader(downloadDirectory)

    init {
        downloadDirectory.deleteRecursively()
    }

    override fun consume(update: Update?) {
        update!!
        println("--- ${LocalDateTime.now()} $update")

        if (canAccept(update)) {
            val text = update.message.text
            val userId = update.message.from.id.toString()
            when (text) {
                "/quand" -> {
                    quand(userId)
                }

                "/start" -> {
                    // No action
                }

                "/cava" -> {
                    sendText("Toujours debout :)", userId)
                }

                "/ride" -> {
                    ride(userId)
                }

                else -> {
                    sendText("'$text' ? Désolé, je ne sais pas quoi dire", userId)
                }
            }
        }
    }

    private fun canAccept(update: Update): Boolean {
        if (update.hasMessage()) {
            if (update.message.isUserMessage) {
                if (update.message.from.id.toString() == adminId) {
                    // Don't ask me why all APIs expect strings but internally, ID for Chat and User are longs
                    // This is why there is a toString() here
                    return true
                } else if (update.message.from.id.toString() in userIds) {
                    sendAdminText("User ${update.message.from.id} is using the bot. Message='${update.message.text}'")
                    return true
                } else {
                    sendAdminText("This message is not from our user => $update")
                }
            } else {
                sendAdminText("This message is not from a user => $update")
            }
        } else {
            sendAdminText("Update has no message => $update")
        }

        return false
    }

    private fun sendAdminText(text: String) {
        assert(text.isNotEmpty()) { "Cannot send an empty text" }
        sendText("ADMIN: $text", adminId)
    }

    private fun sendText(text: String, userID: String) {
        assert(text.isNotEmpty()) { "Cannot send an empty text" }
        val method = SendMessage.builder()
            .chatId(userID)
            .text(text)
            .build()

        telegramClient.execute(method)
    }

    private fun sendFile(file: File, caption: String, userID: String) {
        val method = SendDocument.builder()
            .chatId(userID)
            .document(InputFile(file))
            .caption(caption)
            .build()

        telegramClient.execute(method)
    }

    //------------------------------------------------------------------------------------------------------------------
    // The 'quand' command
    private fun quand(userId: String) {
        val today = LocalDate.now()

        if (today.dayOfWeek == DayOfWeek.WEDNESDAY) {
            sendText("Mais on est mercredi : c'est aujourd'hui qu'on roule !", userId)
        } else {
            var nextRideDay = today.plusDays(1)
            while (nextRideDay.dayOfWeek != DayOfWeek.THURSDAY) {
                nextRideDay = nextRideDay.plusDays(1)
            }
            val delta = Period.between(today, nextRideDay).days - 1
            if (delta == 1) {
                sendText("Demain :)", userId)
            } else {
                sendText(
                    "On est ${today.dayOfWeek.toFrench()}. La prochaine sortie devrait être dans ${delta} jours, le $nextRideDay.",
                    userId
                )
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // The 'ride' command
    private fun ride(userId: String) {
        val today = LocalDate.now()

        when (today.dayOfWeek) {
            DayOfWeek.TUESDAY -> {
                sendText(
                    "On est mardi : le prochain ride, c'est demain. Peut-être que les maps sont déjà en ligne, je vais regarder...",
                    userId
                )
                val tomorrow = today.plusDays(1)
                processDate(tomorrow, userId)
            }

            DayOfWeek.WEDNESDAY -> {
                sendText("C'est jour de ride ! Je vais récupérer les maps!", userId)
                processDate(today, userId)
            }

            else -> {
                sendText(
                    "Désolé, on est encore que ${today.dayOfWeek.toFrench()}... Il faut attendre un peu plus pour la prochaine sortie !",
                    userId
                )
            }
        }
    }

    private fun processDate(date: LocalDate, userId: String) {
        val rides = RideDownloader().day(date)

        when (rides.size) {
            0 -> {
                sendText("Désolé, aucune sortie prévue le ${date}.", userId)
            }

            1 -> {
                processRide(rides[0], userId)
            }

            else -> {
                sendText("Il y a plusieurs sorties (${rides.size}) le ${date}, je ne sais pas quoi faire.", userId)
            }
        }
    }

    private fun processRide(ride: Ride, userId: String) {
        val desiredGroups = setOf(WednesdayGroups.C3, WednesdayGroups.SuperChillGravel)

        sendText("Récupération des maps pour les groupes $desiredGroups pour la sortie ${ride.title}", userId)

        for (group in ride.groups) {
            val guessedGroup = WednesdayGroups.guess(group.name)
            if (guessedGroup in desiredGroups) {
                val file = mapDownloader.download(group.map)
                println("Map for group ${group.name} is here: $file")
                val caption = "Voici la la map pour ${group.name}. RDV à ${group.meetingTime}"
                sendFile(file, caption, userId)
            }
        }
    }
}