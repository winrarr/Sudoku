import java.util.*

open class SudokuGame(sudokuGrid: SudokuGrid, vararg observers: SudokuGUI) {

    private val observers: List<SudokuGUI> = observers.toList()

    private val grid: Array<IntArray> = sudokuGrid.getGrid()
    private val givenNumbers: Array<IntArray> = grid.copyOf()
    private val moveHistory = mutableListOf<Move>()

    private var moveCount = 0

    private var selected: Pair<Int, Int>? = null

    fun setSelected(position: Pair<Int, Int>) {
        selected = position
    }

    fun getSelectedNum(): Int? {
        val selected = selected
        return if (selected != null) {
            getNumAt(selected.first, selected.second)
        } else null
    }

    fun setSelectedNum(num: Int): Boolean {
        if (selectedIsGiven()) return false
        val selected = selected ?: return false
        addMoveToHistory(getSelectedNum()!!, num)
        setNumAt(selected.first, selected.second, num)
        moveCount++
        return true
    }

    private fun selectedIsGiven(): Boolean {
        val selected = selected ?: return false
        return givenNumbers[selected.first][selected.second] != 0
    }

    fun getNumAt(row: Int, col: Int): Int {
        return grid[row][col]
    }

    private fun getSolutionAt(row: Int, col: Int): Int {
        val solution = Solver.solve(grid) ?: return -1
        return solution[row][col]
    }

    private fun setNumAt(row: Int, col: Int, num: Int) {
        grid[row][col] = num
        notifyCellUpdated(row, col)
    }

    private fun setFinalNum(row: Int, col: Int, num: Int) {
        setNumAt(row, col, num)
    }

    fun deleteSelectedNum() {
        setSelectedNum(0)
        moveCount++
    }

    private fun addMoveToHistory(from: Int, to: Int) {
        val selected = selected!!
        val move = Move(selected.first, selected.second, from, to)
        if (moveCount > moveHistory.size) {
            moveHistory.add(move)
        } else {
            moveHistory[moveCount] = move
        }
    }

    fun showRandomSolutionCell() {
        val (row, col) = getRandomBlank()
        setFinalNum(row, col, getSolutionAt(row, col))
    }

    private fun getRandomBlank(): Pair<Int, Int> {
        while (true) {
            val row = Random().nextInt(9)
            val col = Random().nextInt(9)
            if (getNumAt(row, col) == 0) return row to col
        }
    }

     fun undo() {
         val move = moveHistory[moveCount]
         setNumAt(move.row, move.col, move.before)
         moveCount--
    }

    fun redo() {
        moveCount++
        val move = moveHistory[moveCount]
        setNumAt(move.row, move.col, move.after)
    }

    fun isSolved(): Boolean {

        for (row in 0..8) {
            val numbersLeft = (1..9).toMutableList()
            for (col in 0..8) {
                if (!numbersLeft.remove(getNumAt(row, col))) return false
            }
            if (numbersLeft.isNotEmpty()) return false
        }

        for (col in 0..8) {
            val numbersLeft = (1..9).toMutableList()
            for (row in 0..8) {
                if (!numbersLeft.remove(getNumAt(row, col))) return false
            }
            if (numbersLeft.isNotEmpty()) return false
        }

        for (boxRow in 0..2) {
            for (boxCol in 0..2) {
                val numbersLeft = (1..9).toMutableList()
                for (row in (3*boxRow)..(3*boxRow+1)) {
                    for (col in (3*boxCol)..(3*boxCol+1)) {
                        if (!numbersLeft.remove(getNumAt(row, col))) return false
                    }
                }
                if (numbersLeft.isNotEmpty()) return false
            }
        }

        return true

    }

    private fun notifyCellUpdated(row: Int, col: Int) {
        observers.forEach { it.updateSquareAt(row, col) }
    }
}

class Move(val row: Int, val col: Int, val before: Int, val after: Int)