package data

import org.json.JSONObject

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
