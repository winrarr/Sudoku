class SudokuGridStub : SudokuGrid {

    override fun getGrid(): Array<IntArray> {
        return arrayOf(
            intArrayOf(3, 2, 0, 8, 0, 7, 0, 9, 6),
            intArrayOf(0, 0, 0, 0, 6, 0, 0, 0, 0),
            intArrayOf(0, 1, 0, 9, 0, 2, 0, 4, 0),
            intArrayOf(8, 0, 2, 0, 0, 0, 5, 0, 1),
            intArrayOf(0, 3, 0, 0, 0, 0, 0, 8, 0),
            intArrayOf(1, 0, 4, 0, 0, 0, 9, 0, 7),
            intArrayOf(0, 6, 0, 1, 0, 4, 0, 7, 0),
            intArrayOf(0, 0, 0, 0, 5, 0, 0, 0, 0),
            intArrayOf(2, 4, 0, 6, 0, 9, 0, 5, 3)
        )
    }
}