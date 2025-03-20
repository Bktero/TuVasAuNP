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

        val expected = loadResourceFile("feed.txt")
        assertEquals(expected, output)
    }
}
