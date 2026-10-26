import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.longpolling.util.DefaultLongPollingUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendLocation
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.nio.file.Path
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period

private const val FACE = "\uD83D\uDE32" // Unicode = Astonished Face

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

class Bot(
    private val telegramClient: TelegramClient,
    private val adminId: String,
    private val userIds: List<String>,
    private val superchillGravelFinder: SuperchillGravelFinder,
) : DefaultLongPollingUpdateConsumer() {
    private var updateCount = 0
    private var acceptedUpdateCount = 0
    private val logger = KotlinLogging.logger {}

    override fun consume(update: Update?) {
        update!!
        logger.debug { "--- Consuming update: ${LocalDateTime.now()} $update" }
        updateCount++

        if (canAccept(update)) {
            acceptedUpdateCount++

            val text = update.message.text
            val userId =
                update.message.from.id
                    .toString()
            when (text) {
                "/quand" -> {
                    logger.info { "Received command /quand" }
                    quand(userId)
                }

                "/start" -> {
                    // No action
                }

                "/cava" -> {
                    logger.info { "Received command /cava" }
                    cava(userId)
                }

                "/rattrapage" -> {
                    logger.info { "Received command /rattrapage" }
                    rattrapage(userId)
                }

                "/ride" -> {
                    logger.info { "Received command /ride" }
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
                if (update.message.from.id
                        .toString() == adminId
                ) {
                    // Don't ask me why all APIs expect strings but internally, ID for Chat and User are longs
                    // This is why there is a toString() here
                    return true
                } else if (update.message.from.id
                        .toString() in userIds
                ) {
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

    private fun sendText(
        text: String,
        userID: String,
    ) {
        assert(text.isNotEmpty()) { "Cannot send an empty text" }
        val method =
            SendMessage
                .builder()
                .chatId(userID)
                .text(text)
                .build()

        telegramClient.execute(method)
    }

    private fun sendFile(
        file: Path,
        caption: String,
        userID: String,
    ) {
        val method =
            SendDocument
                .builder()
                .chatId(userID)
                .document(InputFile(file.toFile()))
                .caption(caption)
                .build()

        telegramClient.execute(method)
    }

    // -----------------------------------------------------------------------------------------------------------------
    // The 'cava' command
    private fun cava(userId: String) {
        var message =
            """
            Toujours debout :)

            Updates reçues = $updateCount
            Updates acceptées = $acceptedUpdateCount
            """.trimIndent()

        val instant = ProcessHandle.current().info().startInstant()
        if (instant.isPresent) {
            message += "\nDémarrage = ${instant.get()}"
        }

        sendText(message, userId)
    }

    // -----------------------------------------------------------------------------------------------------------------
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
                    "On est ${today.dayOfWeek.toFrench()}. La prochaine sortie devrait être dans $delta jours, le $nextRideDay.",
                    userId,
                )
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // The 'rattrapage' command
    private fun rattrapage(userId: String) {
        val superChillGravel = superchillGravelFinder.findPrevious()
        if (superChillGravel == null) {
            sendText("Désolé, impossible de trouver une session de rattrapage $FACE", userId)
            return
        }
        process(superChillGravel, userId)
    }

    // -----------------------------------------------------------------------------------------------------------------
    // The 'ride' command
    private fun ride(userId: String) {
        val today = LocalDate.now()

        when (today.dayOfWeek) {
            DayOfWeek.TUESDAY -> {
                sendText(
                    "On est mardi : le prochain ride, c'est demain. Peut-être que les maps sont déjà en ligne, je vais regarder...",
                    userId,
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
                    userId,
                )
            }
        }
    }

    private fun processDate(
        date: LocalDate,
        userId: String,
    ) {
        val superChillGravel = superchillGravelFinder.find(date)

        if (superChillGravel == null) {
            sendText("Désolé, impossible de trouver le groupe Superchill Gravel pour le $date $FACE", userId)
            return
        }

        process(superChillGravel, userId)
    }

    private fun process(
        superChillGravel: SuperchillGravel,
        userId: String,
    ) {
        if (superChillGravel.mapFile == null) {
            sendText(
                "Le groupe Superchill Gravel existe, mais il n'a pas de map $FACE",
                userId,
            )
        } else {
            sendFile(superChillGravel.mapFile, "Voici la map. RDV à ${superChillGravel.group.meetingTime}", userId)
        }

        if (superChillGravel.finish == null) {
            sendText("Oh ben... On ne sait pas où est le finish $FACE", userId)
        } else {
            val bar = superChillGravel.finish
            sendText("Le finish sera ici: ${bar.name} => ${bar.address}", userId)

            if (bar.point == null) {
                sendText("Malheureusement, on ne connait pas la location GPS du finish $FACE", userId)
            } else {
                val method =
                    SendLocation
                        .builder()
                        .chatId(userId)
                        .latitude(bar.point.latitude)
                        .longitude(bar.point.longitude)
                        .build()
                telegramClient.execute(method)
            }
        }
    }
}
