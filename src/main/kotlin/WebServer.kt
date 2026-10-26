import io.ktor.http.ContentType
import io.ktor.http.encodeURLPath
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.escapeHTML
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.concurrent.Volatile
import kotlin.io.path.Path
import kotlin.io.path.extension

class WebServer(
    private val finder: SuperchillGravelFinder,
    private val port: Int,
) {
    @Volatile
    private var superchillGravel: SuperchillGravel? = null

    init {
        synchronize()
    }

    fun start() {
        embeddedServer(
            factory = Netty,
            port = port,
        ) {
            module()
        }.start(wait = false)
    }

    private fun Application.module() {
        routing {

            get("/") {
                synchronize()
                val spg = superchillGravel

                if (spg == null) {
                    call.respondText { "No superchill gravel trace found..." }
                } else {
                    val html = renderIndex(
                        spg.ride.title,
                        spg.group.name,
                        spg.ride.date,
                        spg.finish?.name ?: "On ne sait pas",
                        spg.finish?.address ?: "",
                        spg.mapFile?.fileName.toString(),
                    )
                    call.respondText(html, ContentType.Text.Html)
                }
            }
            get("/index.css") {
                call.respondText(readTextResource("index.css"), ContentType.Text.CSS)
            }
            staticResources("/favicon", "favicon")
            get("/favicon.ico") {
                // Normally not needed on modern browsers, added as a fallback
                logger.warn { "Request made to /favicon.ico" }
                call.respondBytes(readBinResource("favicon/favicon.ico"), ContentType.Image.XIcon)
            }
            get("{filename}") {
                val parameter = call.parameters["filename"] ?: throw BadRequestException("Missing filename parameter")
                val filename = Path(parameter)
                logger.debug { "Trying to download $filename..." }

                val mapFile = superchillGravel?.mapFile
                when {
                    filename.extension != "gpx" -> {
                        logger.error { "File extension is not supported: $filename" }
                        throw BadRequestException("Expecting 'gpx' extension")
                    }

                    mapFile == null -> {
                        logger.error { "Map file is missing" }
                        throw NotFoundException("Map file missing")
                    }

                    mapFile.fileName == filename -> call.respondFile(mapFile.toFile())
                }
            }
        }
    }

    private fun readTextResource(name: String): String =
        Application::class.java.getResource("/$name")?.readText() ?: throw NotFoundException("Cannot load $name")

    private fun readBinResource(name: String): ByteArray =
        Application::class.java.getResource("/$name")?.readBytes() ?: throw NotFoundException("Cannot load $name")

    // Read the template from the classpath and fill the {{placeholders}} in memory
    private fun renderIndex(
        ride: String,
        group: String,
        date: LocalDate,
        finish: String,
        address: String,
        filename: String
    ): String {
        return readTextResource("index.html")
            .replace("{{ride}}", ride.escapeHTML())
            .replace("{{group}}", group.escapeHTML())
            .replace("{{date}}", date.toString())
            .replace("{{finish}}", finish.escapeHTML())
            .replace("{{address}}", address.escapeHTML())
            .replace("{{urlFilename}}", filename.encodeURLPath())
            .replace("{{filename}}", filename.escapeHTML())
    }

    private fun synchronize() {
        val today = LocalDate.now()
        when (today.dayOfWeek) {
            DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY -> {
                // Maps are generally published on Tuesday
                superchillGravel = finder.findNext() ?: finder.findPrevious()
            }

            else -> {
                // Don't try to find the next ride, it doesn't exist for sure
                // Since NP's server is slow, don't waste of time requesting it
                // Synchronization delays sending the page back to the super
                superchillGravel = finder.findPrevious()
            }
        }
    }
}
