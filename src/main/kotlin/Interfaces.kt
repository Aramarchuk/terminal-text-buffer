package io.github.aramarchuk.terminalbuffer
interface Content {
    fun setScrollbackSize(maxLines: Int)

    fun getCharAt(column: Int, row: Int): Char? // Get character at position (from screen and scrollback)
    fun getAttributesAt(column: Int, row: Int): TextAttributes? // Get attributes at position
    fun getLineAsString(row: Int): String // Get line as string (from screen and scrollback)

    fun writeChar(x: Int, y: Int, symbol: Symbol)
}

interface Screen {
    fun setWindowSize(width: Int, height: Int)

    fun insertEmptyLine() // Insert an empty line at the bottom of the screen
    fun clearScreen() // Clear the entire screen
    fun clearScreenAndScrollback() // Clear the screen and scrollback

    fun writeText(text: String) // Write text on a line, overriding current content. Moves cursor.
    fun insertText(text: String) // Insert text on a line, possibly wrapping. Moves cursor.
    fun fillLine(character: Char) // Fill a line with a character
    fun clearLine() // Clear a line (equivalent to filling with empty)

    fun getCursorPosition(): Pair<Int, Int> // (column, row)
    fun setCursorPosition(column: Int, row: Int)
    fun moveCursorUp(cells: Int)
    fun moveCursorDown(cells: Int)
    fun moveCursorLeft(cells: Int)
    fun moveCursorRight(cells: Int)

    fun getScreenAsString(): String // Get entire screen content as string
    fun getScreenAndScrollbackAsString(): String // Get entire screen+scrollback content as string

    fun setAttribute(foreground: Int, background: Int, styles: Set<String>)

}

// Main interface combining all capabilities
interface TerminalBuffer :
    Screen,
    Content
