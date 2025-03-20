package biketeam.data

import org.json.JSONObject

/**
 * A dataclass to represent the objects returned by the `/maps` API.
 */
data class Map(
    val id: String,
    val name: String,
    val teamId: String,
    val length: Double,
    val positiveElevation: Double,
    val negativeElevation: Double,
    val postedAt: String,
    val permalink: String,
    val type: String,
    val crossing: Boolean,
    val tags: List<String>
) {
    companion object {
        fun from(json: JSONObject): Map {
            val tagsJson = json.getJSONArray("tags")
            val tags = mutableListOf<String>()
            for (i in 0 until tagsJson.length()) {
                tags.add(tagsJson.getString(i))
            }

            return Map(
                id = json.getString("id"),
                name = json.getString("name"),
                teamId = json.getString("teamId"),
                length = json.getDouble("length"),
                positiveElevation = json.getDouble("positiveElevation"),
                negativeElevation = json.getDouble("negativeElevation"),
                postedAt = json.getString("postedAt"),
                permalink = json.getString("permalink"),
                type = json.getString("type"),
                crossing = json.getBoolean("crossing"),
                tags = tags
            )
        }
    }
}