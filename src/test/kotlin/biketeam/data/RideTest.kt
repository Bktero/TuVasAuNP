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

        output = output.replace("\r\n", "\n")

        val expected = loadResourceFile("rides.txt")
        assertEquals(expected, output)
    }

    @Test
    fun some_groups_may_not_have_a_map() {
        val input = loadJsonArrayFile("rides_with_groups_with_no_map_2025_03_25.json")

        var output = ""
        for (i in 0 until input.length()) {
            val jsonObject = input.getJSONObject(i)
            val ride = Ride.from(jsonObject)
            output += ride.toString() + "\n"
        }

        output = output.replace("\r\n", "\n")

        val expected = loadResourceFile("rides_with_groups_with_no_map.txt")
        assertEquals(expected, output)
    }
}
