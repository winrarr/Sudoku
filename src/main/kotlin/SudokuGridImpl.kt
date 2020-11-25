import util.Solver
import kotlin.random.Random

interface SudokuGrid {
    fun getGrid(): Array<IntArray>
}

class SudokuGridImpl(private val level: Level = Level.EASY): SudokuGrid {

    private var grid = Array(9) { IntArray(9) {0} }

    private fun Array<IntArray>.copy() = Array(size) { get(it).clone() }

    init {
        newGrid()
    }

    override fun getGrid() = grid.copy()

    private fun newGrid() {
        fillDiagonalBoxes()
        fillRemaining(0, 3)
        removeDigits()
    }

    private fun fillDiagonalBoxes() {
        for (i in 0 until 9 step 3) {
            fillBox(i, i)
        }
    }

    private fun fillBox(row: Int, column: Int) {
        val possibleDigits = mutableListOf<Int>()
        possibleDigits.addAll(1..9)

        for (rowOffset in 0 until 3) {
            for (colOffset in 0 until 3) {
                val index = generateRandomInt(possibleDigits.size - 1)
                grid[row + rowOffset][column + colOffset] = possibleDigits[index]
                possibleDigits.removeAt(index)
            }
        }
    }

    private fun generateRandomInt(max: Int) = Random.nextInt(0, max + 1)

    private fun isUnusedInBox(rowStart: Int, columnStart: Int, digit: Int) : Boolean {
        for (row in 0 until 3) {
            for (col in 0 until 3) {
                if (grid[rowStart + row][columnStart + col] == digit) {
                    return false
                }
            }
        }
        return true
    }

    private fun fillRemaining(startRow: Int, startCol: Int) : Boolean {
        var row = startRow
        var col = startCol

        if (col >= 9 && row < 9 - 1) {
            row += 1
            col = 0
        }
        if (row >= 9 && col >= 9) {
            return true
        }
        if (row < 3) {
            if (col < 3) {
                col = 3
            }
        } else if (row < 9 - 3) {
            if (col == (row / 3) * 3) {
                col += 3
            }
        } else {
            if (col == 9 - 3) {
                row += 1
                col = 0
                if (row >= 9) {
                    return true
                }
            }
        }

        for (digit in 1..9) {
            if (isSafeToPutIn(row, col, digit)) {
                grid[row][col] = digit
                if (fillRemaining(row, col + 1)) {
                    return true
                }
                grid[row][col] = 0
            }
        }
        return false
    }

    private fun isSafeToPutIn(row: Int, column: Int, digit: Int) =
        isUnusedInBox(findBoxStart(row), findBoxStart(column), digit)
                && isUnusedInRow(row, digit)
                && isUnusedInColumn(column, digit)

    private fun findBoxStart(index: Int) = index - index % 3

    private fun isUnusedInRow(row: Int, digit: Int) : Boolean {
        for (col in 0 until 9) {
            if (grid[row][col] == digit) {
                return false
            }
        }
        return true
    }

    private fun isUnusedInColumn(column: Int, digit: Int) : Boolean {
        for (row in 0 until 9) {
            if (grid[row][column] == digit) {
                return false
            }
        }
        return true
    }

    private fun removeDigits() {
        val start = System.currentTimeMillis()
        var digitsToRemove = 9 * 9 - level.numberOfProvidedDigits

        while (digitsToRemove > 0) {
            if (System.currentTimeMillis() - start > 2000) {
                grid = Array(9) { IntArray(9) {0} }
                newGrid()
            }
            val randomRow = generateRandomInt(8)
            val randomColumn = generateRandomInt(8)

            if (grid[randomRow][randomColumn] != 0) {
                val digitToRemove = grid[randomRow][randomColumn]
                grid[randomRow][randomColumn] = 0
                if (!Solver.solvable(grid)) {
                    grid[randomRow][randomColumn] = digitToRemove
                } else {
                    digitsToRemove--
                }
            }
        }
    }
}