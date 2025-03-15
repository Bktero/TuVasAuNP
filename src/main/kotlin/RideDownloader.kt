import data.Ride
import org.json.JSONArray
import java.net.URI
import java.time.LocalDate

class RideDownloader {
    private val baseUrl = "https://www.prendslaroue.fr/api/teams/n-peloton/rides"

    fun today(): Ride {
        return this.day(LocalDate.now())
    }

    fun day(date: LocalDate): Ride {
        val array = download("$baseUrl?from=$date&to=$date")
        assert(array.length() == 1)
        return Ride.from(array.getJSONObject(0))
    }

    private fun download(url: String): JSONArray {
        println("Downloading $url...")
        val text = URI(url).toURL().readText()
        return JSONArray(text)
    }

}