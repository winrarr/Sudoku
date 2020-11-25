import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Stage
import tornadofx.*
import java.lang.RuntimeException

interface SudokuObserver {
    fun updateSquareAt(row: Int, col: Int)
    fun hasWon()
    fun mistakeAt(row: Int, col: Int)
}

class SudokuGUI : Application(), SudokuObserver {

    private val selectedBorder = Border(BorderStroke(Color.web("#500050"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
        BorderWidths(2.0, 2.0, 2.0, 2.0, false, false, false, false)))
    private val stdBg = Background(BackgroundFill(Color.web("#121122"), CornerRadii.EMPTY, Insets.EMPTY))
    private var beforeBg = Background(BackgroundFill(Color.web("#121122"), CornerRadii.EMPTY, Insets.EMPTY))
    private val selectedBg = Background(BackgroundFill(Color.web("#1A1832"), CornerRadii.EMPTY, Insets.EMPTY))

    private lateinit var game: SudokuGame
    private var level = Level.EASY
    private var labelArray: Array<Array<Label>> = Array(9) { Array(9) { Label() } }
    private var selected: Pair<Int, Int>? = null

    override fun start(primaryStage: Stage) {
        init(primaryStage)
    }

    private fun init(stage: Stage) {
        startNewGame()

        stage.scene = makeScene(makeRoot())
        stage.minWidth = 700.0
        stage.minHeight = 700.0
        stage.show()
    }

    private fun startNewGame() {
        game = SudokuGameImpl(level)
        game.addObserver(this)
        for (row in 0..8) {
            for (col in 0..8) {
                updateSquareAt(row, col)
            }
        }
    }

    private fun moveSelectedInDirection(code: KeyCode) {
        val selected = selected ?: return
        when (code) {
            KeyCode.UP -> setSelected(selected.first - 1 to selected.second)
            KeyCode.RIGHT -> setSelected(selected.first to selected.second + 1)
            KeyCode.DOWN -> setSelected(selected.first + 1 to selected.second)
            KeyCode.LEFT -> setSelected(selected.first to selected.second - 1)
            else -> throw RuntimeException("Unexpected direction, keycode: $code")
        }
    }

    private fun setSelected(position: Pair<Int, Int>) {
        val selected = selected
        if (selected != null) {
            val label = labelArray[selected.first][selected.second]
            label.background = beforeBg
            label.border = makeBorder(selected)
        }
        val selectedLabel = labelArray[position.first][position.second]
        beforeBg = selectedLabel.background
        selectedLabel.background = selectedBg
        selectedLabel.border = selectedBorder
        this.selected = position
        game.setSelected(position)
    }


    // Window elements and layout

    private fun makeScene(parent: Parent): Scene {
        val scene = Scene(parent, 500.0, 500.0)

        scene.stylesheets.add(javaClass.getResource("styles.css").toExternalForm())

        scene.setOnKeyPressed { e ->
            when {
                e.code == KeyCode.UP || e.code == KeyCode.RIGHT || e.code == KeyCode.DOWN || e.code == KeyCode.LEFT ->
                    moveSelectedInDirection(e.code)
                e.text.matches("\\d".toRegex()) && game.getSelectedNum() != null -> game.setSelectedNum(e.text.toInt())
                e.code == KeyCode.DELETE -> game.deleteSelectedNum()
                e.isControlDown && e.text == "z" -> game.undo()
                e.isControlDown && e.text == "y" -> game.redo()
                e.isControlDown && e.code == KeyCode.SPACE -> {
                    game.showRandomSolutionCell()
                    val selected = selected
                    if (selected != null) {
                        beforeBg = labelArray[selected.first][selected.second].background
                    }
                }
            }
        }

        return scene
    }

    private fun makeRoot(): Parent {
        val menuBar = makeMenuBar()
        val root = VBox(menuBar)

        val gridPane = GridPane()
        root.add(gridPane)

        for (row in 0..8) {
            for (col in 0..8) {
                val square = makeLabel(row, col)
                gridPane.add(square, col, row)
                square.fitToParentSize()
            }
        }

        return root
    }

    private fun makeMenuBar(): MenuBar {
        // Menu bar
        val menuBar = MenuBar()
        menuBar.border = Border(BorderStroke(Color.web("#53517d"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths(1.0, 1.0, 1.0, 1.0, false, false, false, false)))

        // File menu
        val fileMenu = Menu("File")
        menuBar.menus.add(fileMenu)

        val newGame = MenuItem("New game")
        newGame.setOnAction {
            startNewGame()
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
            startNewGame()
        }
        levelMenu.items.add(easy)

        val medium = MenuItem("Medium")
        medium.setOnAction {
            level = Level.MEDIUM
            startNewGame()
        }
        levelMenu.items.add(medium)

        val hard = MenuItem("Hard")
        hard.setOnAction {
            level = Level.HARD
            startNewGame()
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
                """CTRL + SPACE:     Hint (fill out a random square)\n +
                    DELETE:     Delete the number at the selected square\n +
                    CTRL + Z:     Undo\n +
                    CTRL + Y:     Redo"""
            alert.showAndWait()
        }
        helpMenu.items.add(shortcuts)

        return menuBar
    }

    private fun makeLabel(row: Int, col: Int): Label {
        val square = labelArray[row][col]
        labelArray[row][col]

        square.setOnMouseClicked { setSelected(row to col) }

        square.border = makeBorder(row to col)
        square.font = Font.font(30.0)
        square.alignment = Pos.CENTER

        return square
    }

    private fun makeBorder(position: Pair<Int, Int>): Border {
        val (row, col) = position

        val topColour = if (row % 3 == 0) Color.web("#53517d") else Color.web("#22253c")
        val rightColour = if (col % 3 == 2) Color.web("#53517d") else Color.web("#22253c")
        val botColour = if (row % 3 == 2) Color.web("#53517d") else Color.web("#22253c")
        val leftColour = if (col % 3 == 0) Color.web("#53517d") else Color.web("#22253c")

        return Border(
            BorderStroke(topColour, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths(1.0, 0.0, 0.0, 0.0, false, false, false, false)),
            BorderStroke(rightColour, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths(0.0, 1.0, 0.0, 0.0, false, false, false, false)),
            BorderStroke(botColour, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths(0.0, 0.0, 1.0, 0.0, false, false, false, false)),
            BorderStroke(leftColour, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths(0.0, 0.0, 0.0, 1.0, false, false, false, false))
        )
    }


    // Observer

    override fun updateSquareAt(row: Int, col: Int) {
        val num = game.getNumAt(row, col)
        val label = labelArray[row][col]
        label.text = if (num == 0) "" else num.toString()
        beforeBg = stdBg
    }

    override fun hasWon() {
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "Complete"
        alert.headerText = null
        alert.contentText = "Congratulations! You have solved the sudoku"
        alert.showAndWait()
    }

    override fun mistakeAt(row: Int, col: Int) {
        labelArray[row][col].background = Background(BackgroundFill(Color.web("#220f1e"), CornerRadii.EMPTY, Insets.EMPTY))
    }
}