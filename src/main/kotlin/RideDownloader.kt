import data.Ride
import org.json.JSONArray
import java.net.URI
import java.time.LocalDate

class RideDownloader {
    private val baseUrl = "https://www.prendslaroue.fr/api/teams/n-peloton/rides"

    fun today(): List<Ride> {
        return this.day(LocalDate.now())
    }

    fun day(date: LocalDate): List<Ride> {
        val array = download("$baseUrl?from=$date&to=$date")
        val rides = mutableListOf<Ride>();
        for (i in 0 until array.length()) {
            rides.add(Ride.from(array.getJSONObject(i)))
        }
        rides.size
        return rides
    }

    private fun download(url: String): JSONArray {
        println("Downloading $url...")
        val text = URI(url).toURL().readText()
        return JSONArray(text)
    }

}