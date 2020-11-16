import java.util.*

open class SudokuGame(sudokuGrid: SudokuGrid, vararg observers: SudokuGUI) {

    private val observers: List<SudokuGUI> = observers.toList()

    private val solution: Array<IntArray> = sudokuGrid.getSolution()
    private val grid: Array<Array<Cell>> = sudokuGrid.getSolution().map { row -> row.map { i -> if (i == 0) Cell(i, false) else Cell(i, true) }.toTypedArray() }.toTypedArray()
    private val moveHistory = mutableListOf<Move>()
    private val unsolvedSquares = mutableListOf<Pair<Int, Int>>()

    private var moveCount = 0

    private var selected: Pair<Int, Int>? = null

    private val selectedNum = getSelectedCell()?.num

    fun getSelectedCell(): Cell? {
        val selected = selected
        return if (selected != null) {
            getCellAt(selected.first, selected.second)
        } else null
    }

    fun getCellAt(row: Int, col: Int): Cell {
        return grid[row][col]
    }

    open fun getSolutionForPos(row: Int, col: Int): Int {
        return solution[row][col]
    }

    private fun setSelectedCellNum(num: Int) {
        setCellAt(selected.first, selected.second, num)
        moveCount++
    }

    private fun setCellAt(row: Int, col: Int, num: Int, solution: Boolean = false) {
        if (selectedNum == null) return
        addMoveToHistory(selectedNum, num)
        getCellAt(row, col).num = num
        notifyCellUpdated(row, col)
    }

    private fun setFinalCell(row: Int, col: Int, num: Int) {
        setCellAt(row, col, num, true)
    }

    fun delete() {
        setSelectedCellNum(0)
        moveCount++
    }

    private fun addMoveToHistory(from: Int, to: Int) {
        val move = Move(selected, from, to)
        if (moveCount > moveHistory.size) {
            moveHistory.add(move)
        } else {
            moveHistory[moveCount] = move
        }
    }

    fun showRandomSolutionCell() {
        val row = Random().nextInt(9)
        val col = Random().nextInt(9)

        setFinalCell(row, col, getSolutionForPos(row, col))
    }

     fun undo() {
         val move = moveHistory[moveCount]
         setCellAt(move.position.first, move.position.second, move.before)
         moveCount--
    }

    fun redo() {
        moveCount++
        val move = moveHistory[moveCount]
        setCellAt(move.position.first, move.position.second, move.after)
    }

    fun isSolved() = unsolvedSquares.isEmpty()

    private fun notifyCellUpdated(row: Int, col: Int) {
        observers.forEach { it.updateSquareAt(row, col) }
    }
}

class Move(val position: Pair<Int, Int>, val before: Int, val after: Int)

class Cell(var num: Int, var solution: Boolean = true)