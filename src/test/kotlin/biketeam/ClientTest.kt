package biketeam

import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.time.DayOfWeek
import java.time.LocalDate
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
        assertEquals(date.dayOfWeek, DayOfWeek.WEDNESDAY)

        val dir = Files.createTempDirectory("tuvasaunp-tests").toFile();
        val client = Client(dir);

        val rides = client.requestRides(date)
        assertEquals(1, rides.size)

        assert(rides[0].groups.isNotEmpty());
        val file = client.requestMapFile(rides[0].groups[0].map)
        assert(file.exists())
        assert(!file.isDirectory)
    }

    @Test
    fun rides_without_results() {
        val date = LocalDate.of(2025, 3, 20) // there was only NO ride that day
        assertEquals(date.dayOfWeek, DayOfWeek.THURSDAY)
        val rides = Client(File("/tmp")).requestRides(date)
        assertEquals(0, rides.size)
    }

    @Test
    fun feed() {
        val feedEntries = Client(File("/tmp")).requestFeed()
        assert(feedEntries.isNotEmpty())
    }

}
