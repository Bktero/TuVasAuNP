import data.Ride
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate

val DOWNLOAD_DIRECTORY = File("working/downloads")

fun processRide(ride: Ride) {
    val desiredGroups = setOf(WednesdayGroups.C3, WednesdayGroups.SuperChillGravel)

    println("Getting the maps of groups $desiredGroups of ride ${ride.title}")

    for (group in ride.groups) {
        val guessedGroup = WednesdayGroups.guess(group.name)
        if (guessedGroup in desiredGroups) {
            val file = MapDownloader(DOWNLOAD_DIRECTORY).download(group.map)
            println("Map for group ${group.name} is here: $file")
            println("Meeting time is ${group.meetingTime}")
        }
    }
}

fun processDate(date: LocalDate) {
    val rides = RideDownloader().day(date)

    when (rides.size) {
        0 -> {
            println("I'm sorry but there is not ride today...")
        }

        1 -> {
            processRide(rides[0])
        }

        else -> {
            println("There are several (${rides.size}) rides today, I don't know what to do.")
        }
    }
}


fun main() {
    println("Let's go riding!")

    DOWNLOAD_DIRECTORY.deleteRecursively()
    DOWNLOAD_DIRECTORY.mkdirs()

//    val today = LocalDate.now()
    val today = LocalDate.of(2025, 3, 12) // Last ride was on Wed 12 March

    when (today.dayOfWeek) {
        DayOfWeek.TUESDAY -> {
            println("It's Tuesday, the ride is tomorrow, but maybe the maps are already available. Let me check!")
            val tomorrow = today.plusDays(1)
            processDate(tomorrow)
        }

        DayOfWeek.WEDNESDAY -> {
            println("It's ride day! Let me get the maps for you!")
            processDate(today)
        }

        else -> {
            println("Sorry, it's only ${today.dayOfWeek}... You have to wait a little more for the next ride.")
        }
    }
}
