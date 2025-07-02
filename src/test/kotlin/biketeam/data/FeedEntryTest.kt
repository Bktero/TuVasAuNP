package biketeam.data

import loadJsonArrayFile
import loadResourceFile
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FeedEntryTest {
    fun compare(input: String, expected: String) {
        val input = loadJsonArrayFile(input)

        var output = ""
        for (i in 0 until input.length()) {
            val jsonObject = input.getJSONObject(i)
            val entry = FeedEntry.from(jsonObject)
            output += "$entry\n-------------------------------------------------------\n"
        }

        output = output.replace("\r\n", "\n")

        val expected = loadResourceFile(expected)

        assertEquals(expected, output)
    }

    @Test
    fun test() {
        // TODO we may use ApprovalTests here
        compare("feed_2025-03-20.json", "feed_2025-03-20.txt")
    }

    @Test
    fun test_with_null_start_place() {
        // On 2025 April 30th, an exception was raised because a Raymond Ride had "null" as it's start place
        compare("feed_with_null_start_place_2025-04-30.json", "feed_with_null_start_place_2025-04-30.txt")
    }

    @Test
    fun test_with_incomplete_entries() {
        // On July 2nd, many exception were raised because some entries are very "incomplete" and lack many fields
        // When we look in details at the JSON data, we see that there is an imaged entry for NP #550 (from May 2024).
        // After it, there are many old entries that are not display on the website.
        // I don't know why there are not displayed, but it's likely that the format has changed since then,
        // and that the old format had many fields less.
        compare("feed_incomplete_entries_2025-07-02.json", "feed_incomplete_entries_2025-07-02.txt")
    }
}
