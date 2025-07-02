package biketeam.data

import org.json.JSONObject

/**
 * A dataclass to represent the start and end places inside a [FeedEntry].
 */
data class Place(
    val id: String,
    val teamId: String,
    val name: String,
    val address: String,
    val link: String?,
    val point: Point?,
    val startPlace: Boolean,
    val endPlace: Boolean,
) {

    companion object {
        fun from(json: JSONObject) = Place(
            id = json.getString("id"),
            teamId = json.getString("teamId"),
            name = json.getString("name"),
            address = json.getString("address"),
            link = json.optString("link"),
            point = json.optJSONObject("point")?.let {
                Point.from(it)
            },
            startPlace = json.getBoolean("startPlace"),
            endPlace = json.getBoolean("endPlace"),
        )
    }
}
