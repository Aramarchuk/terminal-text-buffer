package io.github.aramarchuk.terminalbuffer


data class CursorPosition(val column: Int, val row: Int)

data class CursorState(var position: CursorPosition = CursorPosition(0, 0), var attr: TextAttributes = TextAttributes.default())

class ScreenImpl(
    private var width: Int,
    private var height: Int,
) : Screen {
    private val cursorState: CursorState = CursorState()
    private val screenContent: List<Line> = List(height) {
        Line(MutableList(width) { Symbol(' ', TextAttributes.default()) }, wrapped = false)
    }

    override fun getCharAt(column: Int, row: Int): Char {
        if (row !in 0 until height || column !in 0 until width) {
            throw IndexOutOfBoundsException(
                "Invalid screen coordinates: row=$row, column=$column (height=$height, width=$width)"
            )
        }
        return screenContent[row].symbols[column].char
    }

    override fun clearScreen() {
        for (row in 0 until height) {
            for (col in 0 until width) {
                screenContent[row].symbols[col] = Symbol(' ', TextAttributes.default())
            }
            screenContent[row].wrapped = false
        }
        cursorState.position = CursorPosition(0, 0)
    }

    override fun writeChar(char: Char) {
        screenContent[cursorState.position.row].symbols[cursorState.position.column] = Symbol(char, cursorState.attr)
    }

    override fun insertChar(char: Char): Symbol? {
        val row = cursorState.position.row
        val col = cursorState.position.column

        val lastSymbol = screenContent[row].symbols[width - 1]

        for (c in width - 1 downTo col + 1) {
            screenContent[row].symbols[c] = screenContent[row].symbols[c - 1]
        }

        screenContent[row].symbols[col] = Symbol(char, cursorState.attr)

        return if (lastSymbol.char != ' ' || lastSymbol.attr != TextAttributes.default()) {
            lastSymbol
        } else {
            null
        }
    }

    override fun scrollUp(): Line {
        val topLine = Line(screenContent[0].symbols.toMutableList(), screenContent[0].wrapped)

        for (row in 0 until height - 1) {
            for (col in 0 until width) {
                screenContent[row].symbols[col] = screenContent[row + 1].symbols[col]
            }
            screenContent[row].wrapped = screenContent[row + 1].wrapped
        }

        for (col in 0 until width) {
            screenContent[height - 1].symbols[col] = Symbol(' ', TextAttributes.default())
        }
        screenContent[height - 1].wrapped = false

        return topLine
    }

    override fun scrollDown() {
        for (row in height - 1 downTo 1) {
            for (col in 0 until width) {
                screenContent[row].symbols[col] = screenContent[row - 1].symbols[col]
            }
            screenContent[row].wrapped = screenContent[row - 1].wrapped
        }

        for (col in 0 until width) {
            screenContent[0].symbols[col] = Symbol(' ', TextAttributes.default())
        }
        screenContent[0].wrapped = false
    }

    override fun moveCursorToNextLine(wrapped: Boolean) {
        cursorState.position = cursorState.position.copy(column = 0)
        if (cursorState.position.row < height - 1) {
            cursorState.position = cursorState.position.copy(row = cursorState.position.row + 1)
        }
        screenContent[cursorState.position.row].wrapped = wrapped
    }

    override fun isAtBottomLine(): Boolean = cursorState.position.row >= height - 1

    override fun isLineWrapped(row: Int): Boolean {
        if (row !in 0 until height) {
            throw IndexOutOfBoundsException("Row $row out of bounds [0, $height)")
        }
        return screenContent[row].wrapped
    }

    override fun getLineAsString(row: Int): String {
        if (row !in 0 until height) {
            throw IndexOutOfBoundsException("Row $row out of bounds [0, $height)")
        }
        return screenContent[row].symbols.joinToString(separator = "") { it.char.toString() }.trimEnd()
    }

    override fun fillLine(char: Char) {
        val row = cursorState.position.row
        for (col in 0 until width) {
            screenContent[row].symbols[col] = Symbol(char, cursorState.attr)
        }
    }

    override fun clearLine() {
        val row = cursorState.position.row
        for (col in 0 until width) {
            screenContent[row].symbols[col] = Symbol(' ', TextAttributes.default())
        }
    }

    override fun getCursorPosition(): CursorPosition {
        return cursorState.position
    }

    override fun setCursorPosition(column: Int, row: Int) {
        cursorState.position = CursorPosition(column, row)
    }

    override fun moveCursorUp(cells: Int) {
        cursorState.position = cursorState.position.copy(row = maxOf(0, cursorState.position.row - cells))
    }

    override fun moveCursorDown(cells: Int) {
        cursorState.position = cursorState.position.copy(row = minOf(height - 1, cursorState.position.row + cells))
    }

    override fun moveCursorLeft(cells: Int) {
        cursorState.position = cursorState.position.copy(column = maxOf(0, cursorState.position.column - cells))
    }

    override fun moveCursorRight(cells: Int) {
        cursorState.position = cursorState.position.copy(column = minOf(width - 1, cursorState.position.column + cells))
    }

    override fun getScreenAsString(): String {
        val sb = StringBuilder()
        for (row in 0 until height) {
            sb.append(getLineAsString(row))
            sb.append("\n")
        }
        return sb.toString()
    }

    override fun setAttributes(attributes: TextAttributes) {
        cursorState.attr = attributes
    }

    override fun getAttributes(): TextAttributes {
        return cursorState.attr
    }

    override fun getAttributesAt(column: Int, row: Int): TextAttributes {
        if (row !in 0 until height || column !in 0 until width) {
            throw IndexOutOfBoundsException(
                "Invalid screen coordinates: row=$row, column=$column (height=$height, width=$width)"
            )
        }
        return screenContent[row].symbols[column].attr
    }
}