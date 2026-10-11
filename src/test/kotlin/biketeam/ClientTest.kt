package biketeam

import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.test.assertEquals

// Simply requests the server
// Not the best test because
// - we don't check the actual result (just that it doesn't crash)
// - the server may be down and the test will fail
// - the server is quite slow to answer
// It's still a cheap and easy way to test the client
class ClientTest {
    @Test
    fun rides_with_results_and_map() {
        val date = LocalDate.of(2025, 3, 19) // there was only one ride that day
        assertEquals(DayOfWeek.WEDNESDAY, date.dayOfWeek)

        val client = Client()

        val rides = client.requestRides(date)
        assertEquals(1, rides.size)

        assert(rides[0].groups.isNotEmpty())
        val group = rides[0].groups[0]

        val file = client.requestMapFile(group.map!!) // We now that each group had a map on this day
        assert(file.exists())
        assert(!file.isDirectory())
    }

    @Test
    fun rides_without_results() {
        val date = LocalDate.of(2025, 3, 20) // there was only NO ride that day
        assertEquals(DayOfWeek.THURSDAY, date.dayOfWeek)
        val rides = Client().requestRides(date)
        assertEquals(0, rides.size)
    }

    @Test
    fun feed() {
        val feedEntries = Client().requestFeed()
        assert(feedEntries.isNotEmpty())
    }
}
