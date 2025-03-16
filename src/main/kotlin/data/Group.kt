package data

import org.json.JSONObject
import java.time.LocalTime

// data.Group class (representing a ride group)
data class Group(
    val id: String,
    val name: String,
    val meetingTime: LocalTime,
    val averageSpeed: Int,
    val map: Map,
    val participants: List<Participant>
) {
    companion object {
        fun from(json: JSONObject): Group {
            val participantsJson = json.getJSONArray("participants")
            val participants = mutableListOf<Participant>()
            for (i in 0 until participantsJson.length()) {
                val participantJson = participantsJson.getJSONObject(i)
                participants.add(
                    Participant.from(participantJson)
                )
            }

            return Group(
                id = json.getString("id"),
                name = json.getString("name"),
                meetingTime = LocalTime.parse(json.getString("meetingTime")),
                averageSpeed = json.getInt("averageSpeed"),
                map = Map.from(json.getJSONObject("map")),
                participants = participants
            )
        }
    }
}