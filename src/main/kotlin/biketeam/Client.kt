package biketeam

import biketeam.data.FeedEntry
import biketeam.data.Map
import biketeam.data.Ride
import io.github.oshai.kotlinlogging.KotlinLogging
import org.json.JSONArray
import java.net.URI
import java.nio.file.Path
import java.time.LocalDate
import kotlin.io.path.createTempFile
import kotlin.io.path.writeBytes

class Client {
    private val baseUrl = "https://www.prendslaroue.fr/api/teams/n-peloton"
    private val logger = KotlinLogging.logger {}

    fun requestRides(date: LocalDate): List<Ride> {
        val array = readArray("$baseUrl/rides?from=$date&to=$date")
        val rides = mutableListOf<Ride>()
        for (i in 0 until array.length()) {
            rides.add(Ride.from(array.getJSONObject(i)))
        }
        return rides
    }

    fun requestFeed(): List<FeedEntry> {
        val array = readArray("$baseUrl/feed")
        val entries = mutableListOf<FeedEntry>()
        for (i in 0 until array.length()) {
            entries.add(FeedEntry.from(array.getJSONObject(i)))
        }
        return entries
    }

    fun requestMapFile(map: Map): Path {
        val url = URI("https://www.prendslaroue.fr/n-peloton/maps/${map.id}/gpx").toURL()
        val file = createTempFile("${map.name}-", ".gpx")
        logger.info { "Downloading $url to $file..." }
        val bytes = url.readBytes()
        file.writeBytes(bytes)
        return file
    }

    private fun readArray(url: String): JSONArray {
        logger.info { "Reading array $url..." }
        val text = URI(url).toURL().readText()
        return JSONArray(text)
    }
}
