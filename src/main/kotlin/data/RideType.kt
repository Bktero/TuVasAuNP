package data

enum class RideType {
    REGULAR, SPECIAL, RACE, UNKNOWN;

    companion object {
        fun from(string: String): RideType {
            return RideType.valueOf(string)
        }
    }
}