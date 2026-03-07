package io.github.aramarchuk.terminalbuffer

interface ScreenInternal {
    fun writeChar(char: Char)
    fun insertChar(char: Char): Symbol?
    fun scrollUp(): Line
    fun scrollDown()
    fun moveCursorToNextLine(wrapped: Boolean = false)
    fun isAtBottomLine(): Boolean
    fun getLineAsString(row: Int): String
    fun isLineWrapped(row: Int): Boolean
}
interface ScrollbackPublic {
    fun getCharAt(column: Int, row: Int): Char // Get character at position (from screen and scrollback)
    fun getAttributesAt(column: Int, row: Int): TextAttributes // Get attributes at position
    fun getLineAsString(row: Int): String // Get line as string (from screen and scrollback)
    fun isLineWrapped(row: Int): Boolean // Whether the line is a wrapped continuation
}

interface ScrollbackInternal {
    fun pushLine(line: Line)
    fun getActualSize(): Int
    fun clear()
}

interface Scrollback: ScrollbackPublic, ScrollbackInternal

interface ScreenPublic {
    fun getCharAt(column: Int, row: Int): Char

    fun clearScreen() // Clear the entire screen

    fun clearLine() // Clear a line (equivalent to filling with spaces)
    fun fillLine(char: Char)

    fun getCursorPosition(): CursorPosition
    fun setCursorPosition(column: Int, row: Int)
    fun moveCursorUp(cells: Int)
    fun moveCursorDown(cells: Int)
    fun moveCursorLeft(cells: Int)
    fun moveCursorRight(cells: Int)

    fun getScreenAsString(): String // Get entire screen content as string
    fun getLineAsString(row: Int): String

    fun getAttributesAt(column: Int, row: Int): TextAttributes
    fun setAttributes(attributes: TextAttributes)
    fun getAttributes(): TextAttributes

    fun isLineWrapped(row: Int): Boolean // Whether the line is a wrapped continuation
}

interface Screen: ScreenInternal, ScreenPublic

// Main interface combining all capabilities
interface TerminalBuffer :
    ScreenPublic,
    ScrollbackPublic {

    fun insertEmptyLine()
    fun writeText(text: String) // Write text on a line, overriding current content. Moves cursor.
    fun insertText(text: String) // Insert text on a line, possibly wrapping. Moves cursor.

    fun clearScreenAndScrollback() // Clear the screen and scrollback
    fun getScreenAndScrollbackAsString(): String // Get entire screen+scrollback content as string
    override fun getAttributesAt(column: Int, row: Int): TextAttributes
    override fun getCharAt(column: Int, row: Int): Char
    override fun isLineWrapped(row: Int): Boolean
}

