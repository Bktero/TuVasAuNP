package biketeam.data

import org.junit.jupiter.api.Test
import randomString
import kotlin.test.assertEquals

class PublishedStatusTest {
    @Test
    fun testValidString() {
        assertEquals(PublishedStatus.PUBLISHED, PublishedStatus.from("PUBLISHED"))
        assertEquals(PublishedStatus.UNPUBLISHED, PublishedStatus.from("UNPUBLISHED"))
    }

    @Test
    fun testBadStrings() {
        // Case matters in our code, and Biketeam uses capital letters anyway
        assertEquals(PublishedStatus.UNKNOWN, PublishedStatus.from("published"))
        assertEquals(PublishedStatus.UNKNOWN, PublishedStatus.from("unpublished"))

        assertEquals(PublishedStatus.UNKNOWN, PublishedStatus.from(randomString(9U))) // 9 == length of 'PUBLISHED'
        assertEquals(PublishedStatus.UNKNOWN, PublishedStatus.from(randomString(11U))) // 11 == length of 'UNPUBLISHED'
    }
}
