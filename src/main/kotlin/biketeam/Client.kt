package biketeam

import biketeam.data.FeedEntry
import biketeam.data.Map
import biketeam.data.Ride
import org.json.JSONArray
import java.io.File
import java.net.URI
import java.time.LocalDate

class Client(private val downloadDirectory: File) {
    private val baseUrl = "https://www.prendslaroue.fr/api/teams/n-peloton"

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

    fun requestMapFile(map: Map): File {
        val url = URI("https://www.prendslaroue.fr/n-peloton/maps/${map.id}/gpx").toURL()
        val file = File.createTempFile("${map.name}-", ".gpx", downloadDirectory)
        println("Downloading $url to $file...")
        val bytes = url.readBytes()
        file.writeBytes(bytes)
        return file
    }

    private fun readArray(url: String): JSONArray {
        println("Reading array $url...")
        val text = URI(url).toURL().readText()
        return JSONArray(text)
    }
}
