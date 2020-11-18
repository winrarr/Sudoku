import util.Solver

open class SudokuGame(level: Level) {

    private val observers = mutableListOf<SudokuGUI>()

    private val sudokuGrid = SudokuGrid(level)
    private val grid: Array<IntArray> = sudokuGrid.getGrid()
    private val givenNumbers: Array<IntArray> = sudokuGrid.getGrid()
    private val moveHistory = mutableListOf<Move>()

    private val blanks = mutableSetOf<Pair<Int, Int>>()

    private var moveCount = 0

    private var selected: Pair<Int, Int>? = null

    init {
        for (row in 0..8) {
            for (col in 0..8) {
                if (grid[row][col] == 0) {
                    blanks.add(row to col)
                }
            }
        }
    }

    fun addObserver(observer: SudokuGUI) {
        observers.add(observer)
    }

    fun setSelected(position: Pair<Int, Int>) {
        selected = position
    }

    fun getSelectedNum(): Int? {
        val selected = selected
        return if (selected != null) {
            getNumAt(selected.first, selected.second)
        } else null
    }

    fun getNumAt(row: Int, col: Int): Int {
        return grid[row][col]
    }

    private fun setNumAt(row: Int, col: Int, num: Int) {
        grid[row][col] = num
        blanks.remove(row to col)
        notifyCellUpdated(row, col)
        checkSolved()
    }

    fun setSelectedNum(num: Int): Boolean {
        if (selectedIsGiven()) return false
        val selected = selected ?: return false
        moveCount++
        addMoveToHistory(getSelectedNum()!!, num)
        setNumAt(selected.first, selected.second, num)
        return true
    }

    private fun addMoveToHistory(from: Int, to: Int) {
        val selected = selected!!
        val move = Move(selected.first, selected.second, from, to)
        if (moveCount > moveHistory.size - 1) {
            moveHistory.add(move)
        } else {
            moveHistory[moveCount] = move
        }
    }

    private fun selectedIsGiven(): Boolean {
        val selected = selected ?: return false
        return givenNumbers[selected.first][selected.second] != 0
    }

    private fun deleteAt(row: Int, col: Int) {
        setSelectedNum(0)
        notifyCellUpdated(row, col)
    }

    fun deleteSelectedNum() {
        val selected = selected
        selected?.let { deleteAt(selected.first, selected.second) }
        moveCount++
    }

    fun showRandomSolutionCell(): Pair<Boolean, Boolean> {
        val (row, col) = getRandomBlank() ?: return true to false
        val solution = getSolutionAt(row, col) ?: return false to true
        setNumAt(row, col, solution)
        return true to true
    }

    private fun getRandomBlank(): Pair<Int, Int>? {
        if (blanks.isNotEmpty()) return blanks.random()
        return null
    }

    private fun getSolutionAt(row: Int, col: Int): Int? {
        return Solver.solve(grid)?.let { it[row][col] }
    }

     fun undo() {
         if (moveCount > 0) moveCount--
         val move = moveHistory[moveCount]
         setNumAt(move.row, move.col, move.before)
    }

    fun redo() {
        if (moveCount >= moveHistory.size) return
        val move = moveHistory[moveCount]
        setNumAt(move.row, move.col, move.after)
        moveCount++
    }

    private fun checkSolved() {
        if (isSolved()) {
            observers.forEach(SudokuGUI::hasWon)
        }
    }

    fun isSolved(): Boolean {

        for (row in 0..8) {
            val numbersLeft = (1..9).toMutableSet()
            for (col in 0..8) {
                if (!numbersLeft.remove(getNumAt(row, col))) return false
            }
            if (numbersLeft.isNotEmpty()) return false
        }

        for (col in 0..8) {
            val numbersLeft = (1..9).toMutableSet()
            for (row in 0..8) {
                if (!numbersLeft.remove(getNumAt(row, col))) return false
            }
            if (numbersLeft.isNotEmpty()) return false
        }

        for (boxRow in 0..2) {
            for (boxCol in 0..2) {
                val numbersLeft = (1..9).toMutableSet()
                for (row in (3*boxRow)..(3*boxRow+2)) {
                    for (col in (3*boxCol)..(3*boxCol+2)) {
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