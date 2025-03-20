package biketeam

import biketeam.data.FeedEntry
import biketeam.data.Ride
import org.json.JSONArray
import java.io.File
import java.net.URI
import java.time.LocalDate

class Client(private val downloadDirectory: File) {
    private val baseUrl = "https://www.prendslaroue.fr/api/teams/n-peloton"

    fun rides(date: LocalDate): List<Ride> {
        val array = readArray("$baseUrl/rides?from=$date&to=$date")
        val rides = mutableListOf<Ride>()
        for (i in 0 until array.length()) {
            rides.add(Ride.from(array.getJSONObject(i)))
        }
        return rides
    }

    fun feed(): List<FeedEntry> {
        val array = readArray("$baseUrl/feed")
        val entries = mutableListOf<FeedEntry>()
        for (i in 0 until array.length()) {
            entries.add(FeedEntry.from(array.getJSONObject(i)))
        }
        return entries
    }

    private fun readArray(url: String): JSONArray {
        println("Reading array $url...")
        val text = URI(url).toURL().readText()
        return JSONArray(text)
    }
}
