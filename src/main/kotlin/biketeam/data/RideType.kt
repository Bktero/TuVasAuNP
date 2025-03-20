package biketeam.data

/**
 * The possible types of a [Ride].
 */
enum class RideType {
    REGULAR,
    SPECIAL,
    RACE,
    UNKNOWN // A special value to help compatibility if Biketeam changes and avoid null objects
    ;

    companion object {
        /**
         * Create a ride type from a string.
         *
         * @param string a string (probably coming for a Biketeam API)
         * @return the corresponding ride type
         */
        fun from(string: String): RideType {
            return try {
                RideType.valueOf(string)
            } catch (_: IllegalArgumentException) {
                UNKNOWN
            }
        }
    }
}
