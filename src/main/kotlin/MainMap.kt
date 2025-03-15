import java.io.File
import java.time.LocalDate

fun main() {
    println("Let's go riding!")
    val rides = RideDownloader().today()
    println("There are ${rides.size} ride(s) today")
    for (ride in rides) {
        val group = ride.groups[0]
        val file = MapDownloader(File("working")).download(group.map)
        println("Map for group ${group.name} of ride ${ride.title} is here: $file")
    }
}
