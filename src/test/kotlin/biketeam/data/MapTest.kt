package biketeam.data

import loadJsonArrayFile
import loadResourceFile
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class MapTest {
    @Test
    fun test() {
        // TODO we may use ApprovalTests here
        val input = loadJsonArrayFile("maps_2025-03-20.json")

        var output = ""
        for (i in 0 until input.length()) {
            val jsonObject = input.getJSONObject(i)
            val map = Map.from(jsonObject)
            output += map.toString() + "\n"
        }

        val expected = loadResourceFile("maps.txt")
        assertEquals(expected, output)
    }
}
