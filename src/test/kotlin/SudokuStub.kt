class SudokuStub : SudokuGrid() {

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

    override fun getSolution(): Array<IntArray> {
        return arrayOf(
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
    }
}