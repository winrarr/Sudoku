package util

import util.SolveResult.*
import kotlin.math.pow

object Solver {

    lateinit var grid: Array<IntArray>

    fun solvable(grid: Array<IntArray>): Boolean {
        Solver.grid = grid.copy()
        return solve()
    }

    fun solve(grid: Array<IntArray>): SolveResult {
        Solver.grid = grid.copy()
        val duplicates = findDuplicates()
        return when {
            duplicates.isNotEmpty() -> Fail(duplicates)
            else -> if (solve()) Solution(Solver.grid) else Fail()
        }
    }

    private fun Array<IntArray>.copy() = Array(size) { get(it).clone() }

    private fun solve(row: Int = 0, col: Int = 0): Boolean {
        if (row >= 9) return true
        if (col >= 9) return solve(row+1, 0)
        if (grid[row][col] != 0) return solve(row, col+1)
        val available = getAvailableDigits(row, col)
        for (k in available) {
            grid[row][col] = k
            if (solve(row, col+1)) return true
        }
        grid[row][col] = 0
        return false
    }

    private fun findDuplicates(): Set<Pair<Int, Int>> {
        val duplicates = mutableSetOf<Pair<Int, Int>>()
        for (row in 0..8) {
            for (col in 0..8) {
                val pos = row to col
                val num = grid[row][col]
                if (num == 0) continue

                val duplicatesForPos = mutableSetOf<Pair<Int, Int>>()

                for (col in 0..8) {
                    if (row to col == pos) break
                    if (grid[row][col] == num) duplicatesForPos.add(row to col)
                }

                for (row in 0..8) {
                    if (row to col == pos) break
                    if (grid[row][col] == num) duplicatesForPos.add(row to col)
                }

                val startBoxRow = (row / 3 * 3)
                val startBoxCol = (col / 3 * 3)
                for (boxRow in startBoxRow..startBoxRow+3) {
                    for (boxCol in startBoxCol..startBoxCol+3) {
                        if (row to col == pos) break
                        if (grid[row][col] == num) duplicatesForPos.add(row to col)
                    }
                }

                if (duplicatesForPos.isNotEmpty()) {
                    duplicates.add(row to col)
                    duplicates.addAll(duplicatesForPos)
                }
            }
        }
        return duplicates
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

sealed class SolveResult {
    class Solution(val grid: Array<IntArray>): SolveResult()
    class Fail(val duplicates: Set<Pair<Int, Int>> = setOf()): SolveResult()
}