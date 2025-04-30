package biketeam.data

import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


/**
 * A dataclass to represent the object returned by the `/feed` API.
 *
 * We cannot request a particular entry or set of entries in the feed,
 * because `/feed` doesn't seems support parameters.
 * It simply returns an array of an (apparently) arbitrary number of elements.
 */
data class FeedEntry(
    val feedType: String,
    val id: String,
    val teamId: String,
    val permalink: String,
    val publishedStatus: PublishedStatus,
    val type: String, // TODO type is same as RideType?
    val date: LocalDate,
    val startPlace: Place?,
    val endPlace: Place?,
    val meetingTime: LocalTime,
    val publishedAt: ZonedDateTime,
    val title: String,
    val description: String,
    val imaged: Boolean,
    val numberOfGroups: UInt,
) {
    companion object {
        fun from(json: JSONObject) = FeedEntry(
            feedType = json.getString("feedType"),
            id = json.getString("id"),
            teamId = json.getString("teamId"),
            permalink = json.getString("permalink"),
            publishedStatus = PublishedStatus.from(json.getString("publishedStatus")),
            type = json.getString("type"),
            date = LocalDate.parse(json.getString("date")),
            startPlace = json.optJSONObject("startPlace")?.let {
                Place.from(it)
            },
            endPlace = json.optJSONObject("endPlace")?.let {
                Place.from(it)
            },
            meetingTime = LocalTime.parse(json.getString("meetingTime")),
            publishedAt = ZonedDateTime.parse(json.getString("publishedAt"), DateTimeFormatter.ISO_DATE_TIME),
            title = json.getString("title"),
            description = json.getString("description"),
            imaged = json.getBoolean("imaged"),
            numberOfGroups = json.getInt("numberOfGroups").toUInt(),
        )
    }
}
