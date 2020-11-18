import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Stage
import tornadofx.add
import tornadofx.fitToParentSize

class SudokuGUI(private var game: SudokuGame = SudokuGame(Level.EASY)) : Application() {

    private var level = Level.EASY

    private lateinit var labelArray: Array<Array<Label>>

    private var selected: Pair<Int, Int>? = null

    override fun start(primaryStage: Stage) {
        init(primaryStage)
    }

    private fun init(stage: Stage) {

        // Initialise
        game.addObserver(this)
        labelArray = Array(9) { Array(9) { Label() } }

        val redBorder = Border(BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
            BorderWidths(3.0, 3.0, 3.0, 3.0, false, false, false, false)))

        val menuBar = makeMenuBar()
        val root = VBox(menuBar)

        val gridPane = GridPane()
        root.add(gridPane)
        gridPane.fitToParentSize()

        for (row in 0..8) {
            for (col in 0..8) {
                val square = labelArray[row][col]

                square.setOnMouseClicked {
                    setSelected(row to col)
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

        stage.scene = Scene(root, 500.0, 500.0)

        stage.scene.setOnKeyPressed { e ->
            when {
                e.text.matches("\\d".toRegex()) && game.getSelectedNum() != null -> game.setSelectedNum(e.text.toInt())
                e.code == KeyCode.ESCAPE -> game.deleteSelectedNum()
                e.isControlDown && e.text == "z" -> game.undo()
                e.isControlDown && e.text == "y" -> game.redo()
                e.isControlDown && e.code == KeyCode.SPACE -> {
                    val (solvable, isBlank) = game.showRandomSolutionCell()
                    if (!solvable) {
                        val alert = Alert(Alert.AlertType.ERROR)
                        alert.title = "Unsolvable"
                        alert.headerText = null
                        alert.contentText = "The current sudoku could not be solved, you have made a mistake"
                        alert.showAndWait()
                    } else if (!isBlank) {
                        val alert = Alert(Alert.AlertType.INFORMATION)
                        alert.title = "Complete"
                        alert.headerText = null
                        alert.contentText = "The sudoku is already complete"
                        alert.showAndWait()
                    }
                }
            }
        }

        stage.minWidthProperty().set(500.0)
        stage.minHeightProperty().set(500.0)
        stage.show()
    }

    private fun restart() {
        game = SudokuGame(level)
        game.addObserver(this)
        for (row in 0..8) {
            for (col in 0..8) {
                updateSquareAt(row, col)
            }
        }
    }

    private fun setSelected(position: Pair<Int, Int>) {
        val selected = selected
        if (selected != null) {
            labelArray[selected.first][selected.second].border = getBorder(selected)
        }
        this.selected = position
        game.setSelected(position)
    }

    fun updateSquareAt(row: Int, col: Int) {
        val num = game.getNumAt(row, col)
        labelArray[row][col].text = if (num == 0) "" else num.toString()
    }

    fun hasWon() {
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "Complete"
        alert.headerText = null
        alert.contentText = "Congratulations! You have solved the sudoku"
        alert.showAndWait()
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

    private fun makeMenuBar(): MenuBar {
        // Menu bar
        val menuBar = MenuBar()

        // File menu
        val fileMenu = Menu("File")
        menuBar.menus.add(fileMenu)

        val newGame = MenuItem("New game")
        newGame.setOnAction {
            restart()
        }
        fileMenu.items.add(newGame)

        val undo = MenuItem("Undo")
        undo.setOnAction {
            game.undo()
        }
        fileMenu.items.add(undo)

        val redo = MenuItem("Redo")
        redo.setOnAction {
            game.redo()
        }
        fileMenu.items.add(redo)

        val hint = MenuItem("Hint")
        hint.setOnAction {
            game.showRandomSolutionCell()
        }
        fileMenu.items.add(hint)

        // Level
        val levelMenu = Menu("Level")
        menuBar.menus.add(levelMenu)

        val easy = MenuItem("Easy")
        easy.setOnAction {
            level = Level.EASY
            restart()
        }
        levelMenu.items.add(easy)

        val medium = MenuItem("Medium")
        medium.setOnAction {
            level = Level.MEDIUM
            restart()
        }
        levelMenu.items.add(medium)

        val hard = MenuItem("Hard")
        hard.setOnAction {
            level = Level.HARD
            restart()
        }
        levelMenu.items.add(hard)

        //Help menu
        val helpMenu = Menu("Help")
        menuBar.menus.add(helpMenu)

        val shortcuts = MenuItem("Shortcuts")
        shortcuts.setOnAction {
            val alert = Alert(Alert.AlertType.INFORMATION)
            alert.title = "Shortcuts"
            alert.headerText = null
            alert.contentText =
                "CTRL + SPACE:     Hint (fill out a random square)\n" +
                        "ESCAPE:     Delete the number at the selected square\n" +
                        "CTRL + Z:     Undo\n" +
                        "CTRL + Y:     Redo"
            alert.showAndWait()
        }
        helpMenu.items.add(shortcuts)

        return menuBar
    }
}