internal object Solver {

    lateinit var grid: Array<IntArray>

    fun solvable(grid: Array<IntArray>): Boolean {
        this.grid = grid.copy()
        return solve()
    }

    fun solve(grid: Array<IntArray>): Array<IntArray>? {
        this.grid = grid.copy()
        return if (solve()) {
            this.grid
        } else null
    }

    private fun Array<IntArray>.copy() = Array(size) { get(it).clone() }

    private fun solve(row: Int = 0, col: Int = 0) : Boolean {
        if (row >= 9) return true
        if (col >= 9) return solve(row+1, 0)
        if (grid[row][col] != 0) return solve(row, col+1)
        for (k in getAvailableDigits(row, col)) {
            grid[row][col] = k
            if (solve(row, col+1)) return true
        }
        grid[row][col] = 0
        return false
    }

    private fun getAvailableDigits(row: Int, col: Int) : Iterable<Int> {

        val availableDigits = (1..9).toMutableSet()

        for (col in 0..8) {
            availableDigits.remove(grid[row][col])
        }

        for (row in 0..8) {
            availableDigits.remove(grid[row][col])
        }

        val boxStartRow = row / 3 * 3
        val boxStartCol = col / 3 * 3
        for (row in boxStartRow..boxStartRow+2) {
            for (col in boxStartCol..boxStartCol+2) {
                availableDigits.remove(grid[row][col])
            }
        }

        return availableDigits.asIterable()
    }
}