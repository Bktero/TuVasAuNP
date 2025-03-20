package biketeam

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class WednesdayGroupsTest {
    @Test
    fun testValidGuess() {
        val names = listOf(
            "C0 Fast Pack 🍆",
            "C1 🍋",
            "C2 🍄",
            "C3 🍓",
            "C4 Super Chill 🥦",
            "Chill Gravel 🍇",
            "G1 🌶️",
            "G2 🍑",
            "G3 🍌",
            "G4 🥕",
            "Gravel 🌰",
            "Pignon Fixe ⚙️",
            "Superchill Gravel 🫐"
        )

        val expectedGuesses = listOf(
            WednesdayGroups.C0,
            WednesdayGroups.C1,
            WednesdayGroups.C2,
            WednesdayGroups.C3,
            WednesdayGroups.C4,
            WednesdayGroups.ChillGravel,
            WednesdayGroups.G1,
            WednesdayGroups.G2,
            WednesdayGroups.G3,
            WednesdayGroups.G4,
            WednesdayGroups.Gravel,
            WednesdayGroups.PignonFixe,
            WednesdayGroups.SuperChillGravel,
        )

        for ((name, expected) in names.zip(expectedGuesses)) {
            val actual = WednesdayGroups.guess(name)
            assertEquals(actual, expected)
        }
    }

    @Test
    fun testBadGuess() {
        val names = listOf(
            "Fast Pack 🍆",
            "C un 🍋",
            "Super Chill 🥦",
//            "Gravel Chill 🍇", // TODO Gravel Chill should be incorrect, but since it starts with a recognized name, Gravel is guessed
            "🌶️G1 ",
            "Fixe ⚙️",
            "Superhill Gravel 🫐"
        )

        for (name in names) {
            val actual = WednesdayGroups.guess(name)
            assertEquals(null, actual)
        }
    }
}
