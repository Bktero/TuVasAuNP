package biketeam.data

import org.json.JSONObject

/**
 * A dataclass to represent a geographic point.
 *
 * It's used in [Place] to geolocalize the place.
 */
data class Point(
    val latitude: Double,
    val longitude: Double,
) {
    companion object {
        fun from(json: JSONObject) = Point(
            latitude = json.getDouble("lat"),
            longitude = json.getDouble("lng")
        )
    }
}
