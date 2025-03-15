package data

enum class PublishedStatus {
    PUBLISHED, UNPUBLISHED, UNKNOWN;

    companion object {
        fun from(string: String): PublishedStatus {
            return PublishedStatus.valueOf(string)
        }
    }
}