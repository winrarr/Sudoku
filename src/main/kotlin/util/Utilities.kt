package util

object Utilities {
    fun printArray(array: Array<IntArray>) = run {
        for (row in array) {
            var line = ""
            for (col in row) {
                line += "$col, "
            }
            println(line.substring(0, line.length))
        }
    }
}