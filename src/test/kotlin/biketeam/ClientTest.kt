package biketeam

import org.junit.jupiter.api.Test
import java.io.File
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
    fun rides_with_results() {
        val date = LocalDate.of(2025, 3, 19) // there was only one ride that day
        assertEquals(date.dayOfWeek, DayOfWeek.WEDNESDAY)
        val rides = Client(File("/tmp")).rides(date)
        assertEquals(1, rides.size)
    }

    @Test
    fun rides_without_results() {
        val date = LocalDate.of(2025, 3, 20) // there was only one ride that day
        assertEquals(date.dayOfWeek, DayOfWeek.THURSDAY)
        val rides = Client(File("/tmp")).rides(date)
        assertEquals(0, rides.size)
    }

    @Test
    fun feed() {
        val feedEntries = Client(File("/tmp")).feed()
        assert(feedEntries.isNotEmpty())
    }
}
