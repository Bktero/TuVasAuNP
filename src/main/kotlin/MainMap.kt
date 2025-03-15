import java.io.File

fun main() {
    println("Let's go riding!")
    val ride = RideDownloader().today()
    val group = ride.groups[0]
    val file = MapDownloader(File("working")).download(group.map)
    println("Map for group ${group.name} of ride ${ride.title} is here: $file")
}
