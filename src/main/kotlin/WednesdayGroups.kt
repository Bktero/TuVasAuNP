import data.Group
import java.util.*

enum class WednesdayGroups {
    C0, C1, C2, C3, C4, G1, G2, G3, G4, Gravel, ChillGravel, SuperChillGravel, PignonFixe;

    companion object {
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
                val entryString =  entry.toString().lowercase(Locale.getDefault())
                if (cleanedName.startsWith(entryString)) {
                    return entry
                }
            }

            return null
        }
    }
}