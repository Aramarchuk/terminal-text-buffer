package io.github.aramarchuk.terminalbuffer
interface Scrollback {
    fun getCharAt(column: Int, row: Int): Char // Get character at position (from screen and scrollback)
    fun getAttributesAt(column: Int, row: Int): TextAttributes // Get attributes at position
    fun getLineAsString(row: Int): String // Get line as string (from screen and scrollback)

    fun pushLine(line: List<Symbol>)
}

interface Screen {
    fun clearScreen() // Clear the entire screen

    fun clearLine() // Clear a line (equivalent to filling with spaces)
    fun fillLine(char: Char)

    fun getCursorPosition(): Pair<Int, Int> // (column, row)
    fun setCursorPosition(column: Int, row: Int)
    fun moveCursorUp(cells: Int)
    fun moveCursorDown(cells: Int)
    fun moveCursorLeft(cells: Int)
    fun moveCursorRight(cells: Int)

    fun getScreenAsString(): String // Get entire screen content as string

    fun setAttribute(foreground: Int, background: Int, styles: Set<String>)
}

// Main interface combining all capabilities
interface TerminalBuffer :
    Screen,
    Scrollback {

    fun insertEmptyLine()
    fun writeText(text: String) // Write text on a line, overriding current content. Moves cursor.
    fun insertText(text: String) // Insert text on a line, possibly wrapping. Moves cursor.

    fun clearScreenAndScrollback() // Clear the screen and scrollback
    fun getScreenAndScrollbackAsString(): String // Get entire screen+scrollback content as string
}

