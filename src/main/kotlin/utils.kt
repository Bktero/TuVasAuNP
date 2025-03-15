import org.json.JSONArray

fun JSONArray.prettyString(): String {
    return this.toString(4)
}