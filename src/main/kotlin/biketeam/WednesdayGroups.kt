package biketeam

import java.util.*

/**
 * Possible groups on Wednesday rides.
 *
 * Biketeam don't provide such an enumeration for the groups. Groups simply have a name, which is a string.
 * This enumeration helps to find the groups and hence the maps we want.
 *
 * @constructor Create empty Wednesday groups
 */
enum class WednesdayGroups {
    C0, C1, C2, C3, C4, G1, G2, G3, G4, Gravel, ChillGravel, SuperChillGravel, PignonFixe;

    companion object {
        /**
         * Guess a WednesdayGroups value from a String.
         *
         * This function is likely to break in the future, when new groups are added, or if their usual names change.
         *
         * @param name a string
         * @return the guessed group
         */
        fun guess(name: String): WednesdayGroups? {
            // Generally, the names of the groups on Wednesdays look like this:
            // C0 Fast Pack 🍆
            // C1 🍋
            // C2 🍄
            // C3 🍓
            // C4 Super Chill 🥦
            // Chill Gravel 🍇
            // G1 🌶️
            // G2 🍑
            // G3 🍌
            // G4 🥕
            // Gravel 🌰
            // Pignon Fixe ⚙️
            // Superchill Gravel 🫐

            for (entry in entries) {
                val cleanedName = name.replace(" ", "").lowercase(Locale.getDefault())
                val entryString = entry.toString().lowercase(Locale.getDefault())
                if (cleanedName.startsWith(entryString)) {
                    return entry
                }
            }

            return null
        }
    }
}