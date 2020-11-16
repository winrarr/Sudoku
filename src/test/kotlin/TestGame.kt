import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TestGame {
    @Test
    fun test() {
        assert(true)
    }

    private lateinit var game: SudokuGame

    @BeforeEach
    fun setUp() {
        game = SudokuGame(SudokuStub())
    }

    @Test
    fun testInitialised() {
        var blanks = 0
        for (row in 0..8) {
            for (col in 0..8) {
                val square = game.getCellAt(row, col)
                assert(square.num in 0..9)
                if (square.num != 0) {
                    assert(square.solution)
                } else {
                    blanks++
                }

                assert(game.getSolutionForPos(row, col) in 1..9)
            }
        }
        assert(blanks == 49)
    }

//    @Test
//    fun testInsertAndGet() {
//        assert(game.getCell(5, 5).num != 7)
//        game.setCell(5, 5, 7)
//        assert(game.getCell(5, 5).num == 7)
//    }
}