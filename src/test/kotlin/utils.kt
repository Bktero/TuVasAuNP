import org.json.JSONArray
import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

private val chars: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

/**
 * Generate a random string.
 *
 * @param length the length of the string
 */
fun randomString(length: UInt) = ThreadLocalRandom.current()
    .ints(length.toLong(), 0, chars.size)
    .asSequence()
    .map(chars::get)
    .joinToString("")

/**
 * Load a file from `src/test/resources`.
 *
 * We leverage the fact that the `resources` is added to tests' classpath.
 *
 * @param name the name of the file
 * @return its content
 */
fun loadResourceFile(name: String): String {
    return ClassLoader.getSystemClassLoader().getResource(name)?.readText()
        ?: throw IllegalStateException("Could not find resource: $name")
}

/**
 * Load a file from `src/test/resources` as a JSON array.
 *
 * @param name the name of the file
 * @return its content
 */
fun loadJsonArrayFile(name: String): JSONArray {
    val text = loadResourceFile(name)
    return JSONArray(text)
}
