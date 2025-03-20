package biketeam.data

/**
 * The possible status of elements in Biketeam.
 *
 * [Ride]s, `Trip`s and  ̀Publication`'s have such a field.
 */
enum class PublishedStatus {
    PUBLISHED,
    UNPUBLISHED,
    UNKNOWN // A special value to help compatibility if Biketeam changes and avoid null objects
    ;

    companion object {
        /**
         * Create a status from a string.
         *
         * @param string a string (probably coming for a Biketeam API)
         * @return the corresponding status
         */
        fun from(string: String): PublishedStatus {
            return try {
                PublishedStatus.valueOf(string)
            } catch (_: IllegalArgumentException) {
                UNKNOWN
            }
        }
    }
}
