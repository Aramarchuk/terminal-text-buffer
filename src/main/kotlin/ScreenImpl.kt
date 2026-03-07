package io.github.aramarchuk.terminalbuffer


data class CursorPosition(val column: Int, val row: Int)

data class CursorState(var position: CursorPosition = CursorPosition(0, 0), var attr: TextAttributes = TextAttributes.default())

class ScreenImpl(
    private var width: Int,
    private var height: Int,
) : Screen {
    private val cursorState: CursorState = CursorState(position = CursorPosition(0, height - 1))
    private val screenContent: List<Line> = List(height) {
        Line(MutableList(width) { Symbol(' ', TextAttributes.default()) }, wrapped = false)
    }

    private fun toInternalRow(publicRow: Int): Int {
        if (publicRow !in 0 until height) {
            throw IndexOutOfBoundsException("Row $publicRow out of bounds [0, $height)")
        }
        return height - 1 - publicRow
    }

    private fun toPublicRow(internalRow: Int): Int {
        if (internalRow !in 0 until height) {
            throw IndexOutOfBoundsException("Row $internalRow out of bounds [0, $height)")
        }
        return height - 1 - internalRow
    }

    override fun getCharAt(column: Int, row: Int): Char {
        if (column !in 0 until width) {
            throw IndexOutOfBoundsException(
                "Invalid screen coordinates: row=$row, column=$column (height=$height, width=$width)"
            )
        }
        val internalRow = toInternalRow(row)
        return screenContent[internalRow].symbols[column].char
    }

    override fun clearScreen() {
        for (row in 0 until height) {
            for (col in 0 until width) {
                screenContent[row].symbols[col] = Symbol(' ', TextAttributes.default())
            }
            screenContent[row].wrapped = false
        }
        cursorState.position = CursorPosition(0, height - 1)
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
        return screenContent[toInternalRow(row)].wrapped
    }

    override fun getLineAsString(row: Int): String {
        return screenContent[toInternalRow(row)].symbols.joinToString(separator = "") { it.char.toString() }.trimEnd()
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
        return CursorPosition(cursorState.position.column, toPublicRow(cursorState.position.row))
    }

    override fun setCursorPosition(column: Int, row: Int) {
        cursorState.position = CursorPosition(column, toInternalRow(row))
    }

    override fun moveCursorUp(cells: Int) {
        val publicRow = toPublicRow(cursorState.position.row)
        val newPublicRow = maxOf(0, publicRow - cells)
        cursorState.position = cursorState.position.copy(row = toInternalRow(newPublicRow))
    }

    override fun moveCursorDown(cells: Int) {
        val publicRow = toPublicRow(cursorState.position.row)
        val newPublicRow = minOf(height - 1, publicRow + cells)
        cursorState.position = cursorState.position.copy(row = toInternalRow(newPublicRow))
    }

    override fun moveCursorLeft(cells: Int) {
        cursorState.position = cursorState.position.copy(column = maxOf(0, cursorState.position.column - cells))
    }

    override fun moveCursorRight(cells: Int) {
        cursorState.position = cursorState.position.copy(column = minOf(width - 1, cursorState.position.column + cells))
    }

    override fun getScreenAsString(): String {
        val sb = StringBuilder()
        for (row in height - 1 downTo 0) {
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
        if (column !in 0 until width) {
            throw IndexOutOfBoundsException(
                "Invalid screen coordinates: row=$row, column=$column (height=$height, width=$width)"
            )
        }
        return screenContent[toInternalRow(row)].symbols[column].attr
    }
}