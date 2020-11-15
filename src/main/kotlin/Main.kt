import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Stage
import tornadofx.fitToParentSize
import kotlin.collections.set
import kotlin.random.Random


fun main() {
    Application.launch(SudokuGUI::class.java)
}

class SudokuGUI : Application() {

    private lateinit var solution: Array<IntArray>

    private var moveCount = 0

    // move number -> position, change
    // move number -> (row, col), (beforeNumber, afterNumber)
    private val moveHistory = mutableMapOf<Int, Pair<Pair<Int, Int>, Pair<String, String>>>()

    private val labelMap = mutableMapOf<Pair<Int, Int>, Label>()
    private var selected: Pair<Int, Int>? = null

    private val unsolvedSquaresList = mutableListOf<Pair<Int, Int>>()

    override fun start(primaryStage: Stage) {
        init(primaryStage)
    }

    private fun init(stage: Stage) {
        val s = Sudoku()
        solution = s.getSolution()
        val grid = s.getGrid()

        for (row in 0..8) {
            for (col in 0..8) {
                unsolvedSquaresList.add(Pair(row, col))
            }
        }

        val redBorder = Border(BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
            BorderWidths(3.0, 3.0, 3.0, 3.0, false, false, false, false)))

        val gridPane = GridPane()

        for (i in 0..8) {
            for (j in 0..8) {
                val square = Label()
                labelMap[Pair(i, j)] = square

                square.setOnMouseClicked {
                    if (selected != null) {
                        labelMap[selected]!!.border = getBorder(i, j)
                    }
                    selected = Pair(i, j)
                    square.border = redBorder
                }

                square.border = getBorder(i, j)
                square.font = Font.font(30.0)
                square.alignment = Pos.CENTER
                val gridNum = grid[i][j].toString()
                square.text = if (gridNum != "0") gridNum else ""

                gridPane.add(square, j, i)
                square.fitToParentSize()
            }
        }

        stage.scene = Scene(gridPane, 500.0, 500.0)

        stage.scene.setOnKeyPressed { e ->
            if (e.text.matches("\\d".toRegex()) && selected != null) move(e.text!!)
            else if (e.isControlDown && e.text == "z") undo()
            else if (e.isControlDown && e.text == "y") redo()
            else if (e.isControlDown && e.code == KeyCode.SPACE) showRandomMove()
        }

        stage.show()
    }

    private fun showRandomMove() {
        val position = unsolvedSquaresList[Random.nextInt(unsolvedSquaresList.size)]
        labelMap[position]!!.text = solution[position.first][position.second].toString()
    }

    private fun move( text: String) {
        moveCount++

        val selectedLabel = labelMap[selected]

        val currentNumber = labelMap[selected]!!.text
        val newNumber = text
        moveHistory[moveCount] = Pair(selected!!, Pair(currentNumber, newNumber))

        selectedLabel!!.text = text

        if (solution[selected!!.first][selected!!.second].toString() == text) {
            if (isSolved()) {
                println("DONE! :D:D:D:D:D:D")
            }
            unsolvedSquaresList.remove(selected!!)
        }
    }

    private fun isSolved(): Boolean {
        return unsolvedSquaresList.isEmpty()
    }

    private fun undo() {
        val move = moveHistory[moveCount]
        if (move != null) {
            labelMap[move.first]!!.text = move.second.first
        }
        moveCount--
    }

    private fun redo() {
        moveCount++
        val move = moveHistory[moveCount]
        if (move != null) {
            labelMap[move.first]!!.text = move.second.second
        }
    }

    private fun getBorder(row: Int, col: Int): Border {
        val topSize = if (row % 3 == 0) 2.0 else 1.0
        val rightSize = if (col % 3 == 2) 2.0 else 1.0
        val botSize = if (row % 3 == 2) 2.0 else 1.0
        val leftSize = if (col % 3 == 0) 2.0 else 1.0

        return Border(BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
            BorderWidths(topSize, rightSize, botSize, leftSize, false, false, false, false)))
    }
}