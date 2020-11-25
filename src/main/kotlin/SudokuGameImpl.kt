import util.SolveResult
import util.SolveResult.*
import util.Solver

interface SudokuGame {
    fun addObserver(observer: SudokuObserver)
    fun getNumAt(row: Int, col: Int): Int
    fun setSelected(position: Pair<Int, Int>)
    fun setSelectedNum(num: Int)
    fun getSelectedNum(): Int?
    fun deleteSelectedNum()
    fun undo()
    fun redo()
    fun showRandomSolutionCell()
}

class SudokuGameImpl(level: Level = Level.EASY): SudokuGame {

    private val observers = mutableListOf<SudokuObserver>()

    private val sudokuGrid = SudokuGridImpl(level)
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

    override fun addObserver(observer: SudokuObserver) {
        observers.add(observer)
    }

    override fun setSelected(position: Pair<Int, Int>) {
        selected = position
    }

    override fun getSelectedNum(): Int? {
        val selected = selected
        return selected?.let { getNumAt(selected.first, selected.second) }
    }

    override fun getNumAt(row: Int, col: Int): Int {
        return grid[row][col]
    }

    private fun setNumAt(row: Int, col: Int, num: Int) {
        grid[row][col] = num
        blanks.remove(row to col)
        notifyCellUpdated(row, col)
        checkSolved()
    }

    override fun setSelectedNum(num: Int) {
        if (selectedIsGiven()) return
        val selected = selected ?: return
        moveCount++
        addMoveToHistory(selected, getSelectedNum()!!, num)
        setNumAt(selected.first, selected.second, num)
    }

    private fun addMoveToHistory(position: Pair<Int, Int>, from: Int, to: Int) {
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

    override fun deleteSelectedNum() {
        val selected = selected ?: return
        addMoveToHistory(selected, getSelectedNum()!!, 0)
        deleteAt(selected.first, selected.second)
        moveCount++
    }

    override fun showRandomSolutionCell() {
        val (row, col) = getRandomBlank() ?: return
        when (val solution = Solver.solve(grid)) {
            is Fail -> solution.duplicates.forEach { notifyMistakeAt(it.first, it.second) }
            is Solution -> setNumAt(row, col, solution.grid[row][col])
        }
    }

    private fun getRandomBlank(): Pair<Int, Int>? {
        return if (blanks.isNotEmpty()) blanks.random() else null
    }

     override fun undo() {
         if (moveCount > 0) moveCount--
         val move = moveHistory[moveCount]
         setNumAt(move.row, move.col, move.before)
    }

    override fun redo() {
        if (moveCount >= moveHistory.size) return
        val move = moveHistory[moveCount]
        setNumAt(move.row, move.col, move.after)
        moveCount++
    }

    private fun checkSolved() {
        if (isSolved()) {
            observers.forEach(SudokuObserver::hasWon)
        }
    }

    fun isSolved(): Boolean {

        //Check row
        for (row in 0..8) {
            val numbersLeft = (1..9).toMutableSet()
            for (col in 0..8) {
                if (!numbersLeft.remove(getNumAt(row, col))) return false
            }
            if (numbersLeft.isNotEmpty()) return false
        }

        // Check columns
        for (col in 0..8) {
            val numbersLeft = (1..9).toMutableSet()
            for (row in 0..8) {
                if (!numbersLeft.remove(getNumAt(row, col))) return false
            }
            if (numbersLeft.isNotEmpty()) return false
        }

        // Check boxes
        for (boxRow in 0..2) {
            for (boxCol in 0..2) {
                val numbersLeft = (1..9).toMutableSet()

                val startBoxRow = 3*boxRow
                val startBoxCol = 3*boxCol
                for (row in startBoxRow..startBoxRow+2) {
                    for (col in startBoxCol..startBoxCol+2) {
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

    private fun notifyMistakeAt(row: Int, col: Int) {
        observers.forEach { it.mistakeAt(row,col) }
    }
}

class Move(val row: Int, val col: Int, val before: Int, val after: Int)