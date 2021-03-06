import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TestGame {
    private lateinit var game: SudokuGame

    @BeforeEach
    fun setUp() {
        game = SudokuGame(Level.EASY)
    }

    @Test
    fun testInitialised() {
        var blanks = 0
        for (row in 0..8) {
            for (col in 0..8) {
                val square = game.getNumAt(row, col)
                assert(square in 0..9)
                if (square == 0) {
                    blanks++
                }
            }
        }
        assert(blanks > 30)
    }

    @Test
    fun isWinnable() {
        val moves = arrayOf(
            intArrayOf(3, 2, 5, 8, 4, 7, 1, 9, 6),
            intArrayOf(4, 9, 7, 5, 6, 1, 3, 2, 8),
            intArrayOf(6, 1, 8, 9, 3, 2, 7, 4, 5),
            intArrayOf(8, 7, 2, 4, 9, 6, 5, 3, 1),
            intArrayOf(9, 3, 6, 7, 1, 5, 4, 8, 2),
            intArrayOf(1, 5, 4, 3, 2, 8, 9, 6, 7),
            intArrayOf(5, 6, 3, 1, 8, 4, 2, 7, 9),
            intArrayOf(7, 8, 9, 2, 5, 3, 6, 1, 4),
            intArrayOf(2, 4, 1, 6, 7, 9, 8, 5, 3)
        )

        assert(!game.isSolved())

        for (row in 0..8) {
            for (col in 0..8) {
                game.setSelected(row to col)
                game.setSelectedNum(moves[row][col])
            }
        }

        assert(game.isSolved())
    }
}