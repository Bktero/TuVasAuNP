package biketeam.data

import loadJsonArrayFile
import loadResourceFile
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FeedEntryTest {
    @Test
    fun test() {
        // TODO we may use ApprovalTests here
        val input = loadJsonArrayFile("feed_2025-03-20.json")

        var output = ""
        for (i in 0 until input.length()) {
            val jsonObject = input.getJSONObject(i)
            val entry = FeedEntry.from(jsonObject)
            output += "$entry\n-------------------------------------------------------\n"
        }

        output = output.replace("\r\n", "\n")

        val expected = loadResourceFile("feed_2025-03-20.txt")

        assertEquals(expected, output)
    }

    @Test
    fun test_with_null_start_place() {
        // On 2025 April 30th, an exception was raised because a Raymond Ride had "null" as it's start place
        val input = loadJsonArrayFile("feed_with_null_start_place_2025-04-30.json")

        var output = ""
        for (i in 0 until input.length()) {
            val jsonObject = input.getJSONObject(i)
            val entry = FeedEntry.from(jsonObject)
            output += "$entry\n-------------------------------------------------------\n"
        }

        output = output.replace("\r\n", "\n")

        val expected = loadResourceFile("feed_with_null_start_place_2025-04-30.txt")
        assertEquals(expected, output)
    }
}
