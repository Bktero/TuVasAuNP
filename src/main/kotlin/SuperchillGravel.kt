import biketeam.data.Group
import biketeam.data.Place
import biketeam.data.Ride
import java.nio.file.Path

data class SuperchillGravel(
    val ride: Ride,
    val group: Group,
    val mapFile: Path?,
    val finish: Place?,
)
