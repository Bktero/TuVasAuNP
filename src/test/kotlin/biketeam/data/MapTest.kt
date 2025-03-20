import kotlin.test.Test

class MapTest {
    @Test
    fun testMapData() {
        // Method 1: Using ClassLoader
        val mapJson = this.javaClass.classLoader.getResource("maps_2025-03-20.json")?.readText()
            ?: throw IllegalStateException("Could not find maps_2025-03-20.json")

        // Method 2: Using getResourceAsStream
        val inputStream = this.javaClass.classLoader.getResourceAsStream("maps_2025-03-20.json")
            ?: throw IllegalStateException("Could not find maps_2025-03-20.json")
        val mapJsonAlt = inputStream.bufferedReader().use { it.readText() }

        // Now you can use the JSON content
        println(mapJson)
    }
}
