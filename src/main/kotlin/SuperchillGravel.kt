import biketeam.data.Group
import biketeam.data.Place
import biketeam.data.Ride
import java.nio.file.Path
import java.time.LocalDateTime

data class SuperchillGravel(
    val ride: Ride,
    val group: Group,
    val mapFile: Path?,
    val finish: Place?,
    val retrievedAt: LocalDateTime,
)
