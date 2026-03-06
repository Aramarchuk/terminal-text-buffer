package io.github.aramarchuk.terminalbuffer

data class TextAttributes(
    val fg: Int = 7,
    val bg: Int = 0,
    val flags: Int = 0
)

data class CursorState(var x: Int, var y: Int, var attr: TextAttributes = TextAttributes())

class ScreenImpl(
    private var width: Int,
    private var height: Int,
) : Screen {
    private val cursorState: CursorState = CursorState(0, 0)
    private val screenContent: List<MutableList<Symbol>> = List(height) {
        MutableList(width) { Symbol(' ', TextAttributes()) }
    }

    override fun clearScreen() {
        for (row in 0 until height) {
            for (col in 0 until width) {
                screenContent[row][col] = Symbol(' ', TextAttributes())
            }
        }
        cursorState.x = 0
        cursorState.y = 0
    }

    override fun writeChar(char: Char) {
        screenContent[cursorState.y][cursorState.x] = Symbol(char, cursorState.attr)
    }

    override fun insertChar(char: Char): Symbol? {
        val row = cursorState.y
        val col = cursorState.x

        val lastSymbol = screenContent[row][width - 1]

        for (c in width - 1 downTo col + 1) {
            screenContent[row][c] = screenContent[row][c - 1]
        }

        screenContent[row][col] = Symbol(char, cursorState.attr)

        return if (lastSymbol.char != ' ' || lastSymbol.attr != TextAttributes()) {
            lastSymbol
        } else {
            null
        }
    }

    override fun scrollUp(): List<Symbol> {
        val topLine = screenContent[0].toList()

        for (row in 0 until height - 1) {
            for (col in 0 until width) {
                screenContent[row][col] = screenContent[row + 1][col]
            }
        }

        for (col in 0 until width) {
            screenContent[height - 1][col] = Symbol(' ', TextAttributes())
        }

        return topLine
    }

    override fun scrollDown() {
        for (row in height - 1 downTo 1) {
            for (col in 0 until width) {
                screenContent[row][col] = screenContent[row - 1][col]
            }
        }

        for (col in 0 until width) {
            screenContent[0][col] = Symbol(' ', TextAttributes())
        }
    }

    override fun moveCursorToNextLine() {
        cursorState.x = 0
        if (cursorState.y < height - 1) {
            cursorState.y++
        }
    }

    override fun isAtBottomLine(): Boolean = cursorState.y >= height - 1

    override fun getLineAsString(row: Int): String {
        if (row < 0 || row >= height) {
            error("Row $row out of bounds [0, $height)")
        }
        return screenContent[row].joinToString(separator = "") { it.char.toString() }.trimEnd()
    }

    override fun fillLine(char: Char) {
        val row = cursorState.y
        for (col in 0 until width) {
            screenContent[row][col] = Symbol(char, cursorState.attr)
        }
    }

    override fun clearLine() {
        val row = cursorState.y
        for (col in 0 until width) {
            screenContent[row][col] = Symbol(' ', TextAttributes())
        }
    }

    override fun getCursorPosition(): Pair<Int, Int> {
        return Pair(cursorState.x, cursorState.y)
    }

    override fun setCursorPosition(column: Int, row: Int) {
        cursorState.x = column
        cursorState.y = row
    }

    override fun moveCursorUp(cells: Int) {
        cursorState.y = maxOf(0, cursorState.y - cells)
    }

    override fun moveCursorDown(cells: Int) {
        cursorState.y = minOf(height - 1, cursorState.y + cells)
    }

    override fun moveCursorLeft(cells: Int) {
        cursorState.x = maxOf(0, cursorState.x - cells)
    }

    override fun moveCursorRight(cells: Int) {
        cursorState.x = minOf(width - 1, cursorState.x + cells)
    }

    override fun getScreenAsString(): String {
        val sb = StringBuilder()
        for (row in 0 until height) {
            sb.append(getLineAsString(row))
            sb.append("\n")
        }
        return sb.toString()
    }

    override fun setAttribute(
        foreground: Int,
        background: Int,
        styles: Set<String>
    ) {
        val flags = 0
        cursorState.attr = TextAttributes(foreground, background, flags)
    }
}