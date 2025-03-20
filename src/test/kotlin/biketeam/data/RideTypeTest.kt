package biketeam.data

import org.junit.jupiter.api.Test
import randomString
import kotlin.test.assertEquals

class RideTypeTest {
    @Test
    fun testValidString() {
        assertEquals(RideType.REGULAR, RideType.from("REGULAR"))
        assertEquals(RideType.SPECIAL, RideType.from("SPECIAL"))
        assertEquals(RideType.RACE, RideType.from("RACE"))
    }

    @Test
    fun testBadStrings() {
        // Case matters in our code, and Biketeam uses capital letters anyway
        assertEquals(RideType.UNKNOWN, RideType.from("regular"))
        assertEquals(RideType.UNKNOWN, RideType.from("special"))
        assertEquals(RideType.UNKNOWN, RideType.from("race"))

        assertEquals(RideType.UNKNOWN, RideType.from(randomString(7U))) // 9 == length of 'REGULAR' and 'SPECIAL'
        assertEquals(RideType.UNKNOWN, RideType.from(randomString(4U))) // 4 == length of 'RACE'
    }
}
