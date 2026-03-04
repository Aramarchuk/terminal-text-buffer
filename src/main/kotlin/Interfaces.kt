package org.aramarchuk.terminal

// Setup
interface Setup {
    fun setWindowSize(width: Int, height: Int)
    fun setScrollbackSize(maxLines: Int)
}

// Attributes
interface Attributes {
    fun setAttribute(foreground: Int, background: Int, styles: Set<String>)
}

// Cursor contract
interface Cursor {
    fun getCursorPosition(): Pair<Int, Int> // (column, row)
    fun setCursorPosition(column: Int, row: Int)
    fun moveCursorUp(cells: Int)
    fun moveCursorDown(cells: Int)
    fun moveCursorLeft(cells: Int)
    fun moveCursorRight(cells: Int)
}

// Editing - cursor and attribute dependent
interface Editing {
    fun writeText(text: String) // Write text on a line, overriding current content. Moves cursor.
    fun insertText(text: String) // Insert text on a line, possibly wrapping. Moves cursor.
    fun fillLine(character: Char) // Fill a line with a character
    fun clearLine() // Clear a line (equivalent to filling with empty)
}

// Editing - position independent
interface LineOps {
    fun insertEmptyLine() // Insert an empty line at the bottom of the screen
    fun clearScreen() // Clear the entire screen
    fun clearScreenAndScrollback() // Clear the screen and scrollback
}

// Content Access
interface Content {
    fun getCharAt(column: Int, row: Int): Char // Get character at position (from screen and scrollback)
    fun getAttributesAt(column: Int, row: Int): Map<String, Any> // Get attributes at position
    fun getLineAsString(row: Int): String // Get line as string (from screen and scrollback)
    fun getScreenAsString(): String // Get entire screen content as string
    fun getScreenAndScrollbackAsString(): String // Get entire screen+scrollback content as string
}

// Main interface combining all capabilities
interface TerminalBuffer :
    Setup,
    Attributes,
    Cursor,
    Editing,
    LineOps,
    Content
