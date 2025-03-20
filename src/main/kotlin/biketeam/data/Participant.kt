package biketeam.data

import org.json.JSONObject

/**
 * A dataclass to represent the participants of a [Ride].
 */
data class Participant(
    val id: String,
    val identity: String
) {
    companion object {
        fun from(json: JSONObject): Participant {
            return Participant(
                id = json.getString("id"),
                identity = json.getString("identity")
            )
        }
    }
}
