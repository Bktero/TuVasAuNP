import biketeam.Client
import biketeam.data.Group
import biketeam.data.Ride
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jenetics.jpx.GPX
import org.json.JSONException
import java.io.IOException
import java.nio.file.Path
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolute
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively
import kotlin.io.path.div
import kotlin.io.path.moveTo

@OptIn(ExperimentalPathApi::class)
class SuperchillGravelFinder(val downloadDirectory: Path) {
    private val client = Client()
    private val cache = HashMap<LocalDate, SuperchillGravel>()
    private val logger = KotlinLogging.logger {}

    init {
        // Delete all files downloaded during a previous execution
        downloadDirectory.deleteRecursively()

        // Recreate the download directory
        logger.debug { "Creating directory $downloadDirectory..." }
        downloadDirectory.createDirectories()
    }

    @Synchronized
    fun find(date: LocalDate): SuperchillGravel? {
        val cached = cache[date]

        if (cached != null) {
            logger.debug { "Found cache entry for date $date" }
            return cached
        }

        logger.debug { "No cache entry for $date, downloading..." }
        val downloaded = try {
            download(date)
        } catch (e: IOException) {
            logger.error(e) { "Cannot reach the NP server for $date: ${e.message}" }
            null
        } catch (e: JSONException) {
            logger.error(e) { "Unexpected answer from the NP server for $date: ${e.message}" }
            null
        }

        if (downloaded == null) {
            logger.warn { "Cannot find something to download for $date" }
        } else {
            cache[date] = downloaded
        }

        return downloaded
    }

    fun findNext(): SuperchillGravel? {
        val date = dateOfNextWednesday()
        return find(date)
    }

    fun findPrevious(): SuperchillGravel? {
        var date = dateOfNextWednesday()

        repeat(10) { // max 10 weeks, or max 10 tries if NP server is down or unresponsive
            date = date.minusWeeks(1) // travel back in time
            val spg = find(date)
            if (spg != null) {
                return spg
            }
        }

        return null
    }

    private fun dateOfNextWednesday(): LocalDate {
        var date = LocalDate.now()
        while (date.dayOfWeek != DayOfWeek.WEDNESDAY) {
            date = date.plusDays(1)
        }
        return date
    }

    private fun download(date: LocalDate): SuperchillGravel? {
        val rides = client.requestRides(date)
        val (ride, group) = findSuperchillGravelRideAndGroup(rides) ?: return null

        // Find the map
        val map = if (group.map != null) {
            // Download the file
            val temp = client.requestMapFile(group.map)
            val shortRideTitle = ride.title.replace("N-Peloton", "NP")
            val name = "$shortRideTitle Superchill Gravel"
            val target = downloadDirectory / "$name.gpx"
            temp.moveTo(target)
            logger.debug { "Map is now available here: ${target.absolute()}" }

            // Change the metadata and track name
            val gpx = GPX.read(target)

            val updatedGpx = gpx
                .toBuilder()
                .metadata { metadataBuilder -> metadataBuilder.name(name) }
                .trackFilter()
                .map { track -> track.toBuilder().name(name).build() }
                .build()
                .build()

            GPX.write(updatedGpx, target)

            // Return the file
            target
        } else {
            null
        }

        // Find the bar
        val entries = client.requestFeed()
        val bar = entries
            .find {
                // The title and the date are the same, we have normally found our entry
                it.title == ride.title && it.date == ride.date
            }?.endPlace

        return SuperchillGravel(ride, group, map, bar)
    }

    private fun findSuperchillGravelRideAndGroup(rides: List<Ride>): Pair<Ride, Group>? {
        for (ride in rides) {
            for (group in ride.groups) {
                val name = group.name.lowercase()
                if (name.contains("super") && name.contains("chill") && name.contains("gravel")
                ) {
                    return Pair(ride, group)
                }
            }
        }

        return null
    }
}
