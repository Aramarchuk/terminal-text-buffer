package io.github.aramarchuk.terminalbuffer


data class CursorState(var x: Int, var y: Int, var attr: TextAttributes = TextAttributes.default())

class ScreenImpl(
    private var width: Int,
    private var height: Int,
) : Screen {
    private val cursorState: CursorState = CursorState(0, 0)
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
        cursorState.x = 0
        cursorState.y = 0
    }

    override fun writeChar(char: Char) {
        screenContent[cursorState.y].symbols[cursorState.x] = Symbol(char, cursorState.attr)
    }

    override fun insertChar(char: Char): Symbol? {
        val row = cursorState.y
        val col = cursorState.x

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
        cursorState.x = 0
        if (cursorState.y < height - 1) {
            cursorState.y++
        }
        if (wrapped) {
            screenContent[cursorState.y].wrapped = true
        }
    }

    override fun isAtBottomLine(): Boolean = cursorState.y >= height - 1

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
        val row = cursorState.y
        for (col in 0 until width) {
            screenContent[row].symbols[col] = Symbol(char, cursorState.attr)
        }
    }

    override fun clearLine() {
        val row = cursorState.y
        for (col in 0 until width) {
            screenContent[row].symbols[col] = Symbol(' ', TextAttributes.default())
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