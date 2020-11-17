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

class SudokuGUI(private val game: SudokuGame) : Application() {

    private lateinit var solution: Array<IntArray>

    private var moveCount = 0

    // move number -> position, change
    // move number -> (row, col), (beforeNumber, afterNumber)
    private val moveHistory = mutableListOf<Move>()

    private val labelMap = Array(9) { Array(9) { Label() } }

    private var selected: Pair<Int, Int>? = null
    private val selectedLabel = labelMap[selected!!.first][selected!!.second]

    private fun getLabel(position: Pair<Int, Int>) = labelMap[position.first][position.second]

    private val unsolvedSquaresList = mutableListOf<Pair<Int, Int>>()

    override fun start(primaryStage: Stage) {
        init(primaryStage)
    }

    private fun init(stage: Stage) {
        for (row in 0..8) {
            for (col in 0..8) {
                unsolvedSquaresList.add(row to col)
            }
        }

        val redBorder = Border(BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
            BorderWidths(3.0, 3.0, 3.0, 3.0, false, false, false, false)))

        val gridPane = GridPane()

        for (row in 0..8) {
            for (col in 0..8) {
                val square = labelMap[row][col]

                square.setOnMouseClicked {
                    if (game.getSelectedNum() != null) {
                        selectedLabel.border = getBorder(selected!!)
                    }
                    selected = row to col
                    square.border = redBorder
                }

                square.border = getBorder(row to col)
                square.font = Font.font(30.0)
                square.alignment = Pos.CENTER
                val gridNum = game.getNumAt(row, col)
                square.text = if (gridNum != 0) gridNum.toString() else ""

                gridPane.add(square, col, row)
                square.fitToParentSize()
            }
        }

        stage.scene = Scene(gridPane, 500.0, 500.0)

        stage.scene.setOnKeyPressed { e ->
            if (e.text.matches("\\d".toRegex()) && game.getSelectedNum() != null) game.setSelectedNum(e.text.toInt())
            else if (e.code == KeyCode.ESCAPE) game.deleteSelectedNum()
            else if (e.isControlDown && e.text == "z") game.undo()
            else if (e.isControlDown && e.text == "y") game.redo()
            else if (e.isControlDown && e.code == KeyCode.SPACE) game.showRandomSolutionCell()
        }

        stage.show()
    }

    private fun getBorder(position: Pair<Int, Int>): Border {
        val row = position.first
        val col = position.second

        val topSize = if (row % 3 == 0) 2.0 else 1.0
        val rightSize = if (col % 3 == 2) 2.0 else 1.0
        val botSize = if (row % 3 == 2) 2.0 else 1.0
        val leftSize = if (col % 3 == 0) 2.0 else 1.0

        return Border(BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
            BorderWidths(topSize, rightSize, botSize, leftSize, false, false, false, false)))
    }

    fun updateSquareAt(row: Int, col: Int) {
        labelMap[row][col].text = game.getNumAt(row, col).toString()
    }
}