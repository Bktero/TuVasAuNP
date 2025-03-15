package data

import org.json.JSONObject
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

// Main Ride class
data class Ride(
    val id: String,
    val date: LocalDate,
    val title: String,
    val description: String,
    val teamId: String,
    val permalink: String,
    val type: RideType,
    val imaged: Boolean,
    val publishedAt: ZonedDateTime,
    val publishedStatus: PublishedStatus,
    val groups: List<Group>
) {
    companion object {
        fun from(json: JSONObject): Ride {
            val groupsJsonArray = json.getJSONArray("groups")
            val groups = mutableListOf<Group>()

            for (i in 0 until groupsJsonArray.length()) {
                val groupJson = groupsJsonArray.getJSONObject(i)
                groups.add(Group.from(groupJson))
            }

            return Ride(
                id = json.getString("id"),
                date = LocalDate.parse(json.getString("date")),
                title = json.getString("title"),
                description = json.getString("description"),
                teamId = json.getString("teamId"),
                permalink = json.getString("permalink"),
                type = RideType.from(json.getString("type")),
                imaged = json.getBoolean("imaged"),
                publishedAt = ZonedDateTime.parse(json.getString("publishedAt"), DateTimeFormatter.ISO_DATE_TIME),
                publishedStatus = PublishedStatus.from(json.getString("publishedStatus")),
                groups = groups
            )
        }
    }
}