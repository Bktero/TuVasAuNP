package biketeam.data

import loadJsonArrayFile
import loadResourceFile
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RideTest {
    @Test
    fun test() {
        // TODO we may use ApprovalTests here
        val input = loadJsonArrayFile("rides_2025-03-20.json")

        var output = ""
        for (i in 0 until input.length()) {
            val jsonObject = input.getJSONObject(i)
            val ride = Ride.from(jsonObject)
            output += ride.toString() + "\n"
        }

        val expected = loadResourceFile("rides.txt")
        assertEquals(expected, output)
    }
}
