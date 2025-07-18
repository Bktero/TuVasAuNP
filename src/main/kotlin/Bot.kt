import biketeam.Client
import biketeam.WednesdayGroups
import biketeam.data.Ride
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendLocation
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import kotlin.system.exitProcess

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
    private val biketeamClient = Client(downloadDirectory)
    private var updateCount = 0
    private var acceptedUpdateCount = 0

    init {
        // Delete all files downloaded during a previous execution
        downloadDirectory.deleteRecursively()

        // Recreate the download directory
        println("Creating directory $downloadDirectory...")
        val created = downloadDirectory.mkdirs()

        if (created) {
            println("OK")
        } else {
            println("Failure")
            exitProcess(1)
        }
    }

    override fun consume(update: Update?) {
        update!!
        println("--- ${LocalDateTime.now()} $update")
        updateCount++

        if (canAccept(update)) {
            acceptedUpdateCount++

            val text = update.message.text
            val userId = update.message.from.id.toString()
            when (text) {
                "/quand" -> {
                    println("Received command /quand")
                    quand(userId)
                }

                "/start" -> {
                    // No action
                }

                "/cava" -> {
                    println("Received command /cava")
                    cava(userId)
                }

                "/ride" -> {
                    println("Received command /ride")
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
        assert(text.isNotEmpty()) { "Cannot send an empty text to admin" }
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
    // The 'cava' command
    private fun cava(userId: String) {
        var message = """
            |Toujours debout :)
            |
            |Updates reçues = $updateCount
            |Updates acceptées = $acceptedUpdateCount
            """.trimMargin()

        val instant = ProcessHandle.current().info().startInstant()
        if (instant.isPresent) {
            message += "\nDémarrage = ${instant.get()}"
        }

        sendText(message, userId)
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
                    "Désolé, on est encore que ${today.dayOfWeek.toFrench()}... Il faut attendre un peu pour la prochaine sortie !",
                    userId
                )
            }
        }
    }

    private fun processDate(date: LocalDate, userId: String) {
        val rides = biketeamClient.requestRides(date)

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
        // First, get the map
        val desiredGroups = setOf(WednesdayGroups.C3, WednesdayGroups.SuperChillGravel)
        sendText("Récupération des maps pour les groupes $desiredGroups pour la sortie ${ride.title}", userId)

        for (group in ride.groups) {
            val guessedGroup = WednesdayGroups.guess(group.name)
            if (guessedGroup in desiredGroups) {
                if (group.map != null) {
                    val file = biketeamClient.requestMapFile(group.map)
                    println("Map for group ${group.name} is here: $file")
                    val caption = "Voici la la map pour ${group.name}. RDV à ${group.meetingTime}"
                    sendFile(file, caption, userId)
                } else {
                    sendText(
                        "Le groupe ${group.name} existe, mais il n'a pas de map \uD83D\uDE32", // Unicode = Astonished Face
                        userId
                    )
                }
            }
        }

        // Second, get the bar
        val entries = biketeamClient.requestFeed()
        for (entry in entries) {
            if (entry.title == ride.title && entry.date == ride.date) {
                // The title and the date are the same, we have normally found our entry
                when (entry.endPlace) {
                    null -> {
                        sendText("Oh ben... On ne sait pas où est le finish :(", userId)
                    }

                    else -> {
                        val name = entry.endPlace.name
                        val address = entry.endPlace.address
                        sendText("Le finish sera ici: $name => $address", userId)

                        when (entry.endPlace.point) {
                            null -> {
                                sendText("On ne connait pas la location GPS du finish, désolé ;)", userId)
                            }

                            else -> {
                                val method = SendLocation
                                    .builder()
                                    .chatId(userId)
                                    .latitude(entry.endPlace.point.latitude)
                                    .longitude(entry.endPlace.point.longitude)
                                    .build()
                                telegramClient.execute(method)
                            }
                        }
                    }
                }
            }
        }
    }
}
