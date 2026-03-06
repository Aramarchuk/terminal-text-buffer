package io.github.aramarchuk.terminalbuffer

class TerminalBufferImpl(
    val width: Int,
    val height: Int,
    val scrollbackSize: Int
) : TerminalBuffer {
    private val screen = ScreenImpl(width, height)
    private val scrollback = ScrollbackImpl(scrollbackSize)

    override fun clearScreen() = screen.clearScreen()
    override fun clearScreenAndScrollback() = screen.clearScreenAndScrollback()
    override fun clearLine() = screen.clearLine()
    override fun fillLine(char: Char) = screen.fillLine(char)
    override fun getCursorPosition() = screen.getCursorPosition()
    override fun setCursorPosition(column: Int, row: Int) = screen.setCursorPosition(column, row)
    override fun moveCursorUp(cells: Int) = screen.moveCursorUp(cells)
    override fun moveCursorDown(cells: Int) = screen.moveCursorDown(cells)
    override fun moveCursorLeft(cells: Int) = screen.moveCursorLeft(cells)
    override fun moveCursorRight(cells: Int) = screen.moveCursorRight(cells)
    override fun getScreenAsString() = screen.getScreenAsString()
    override fun getScreenAndScrollbackAsString() = screen.getScreenAndScrollbackAsString()
    override fun setAttribute(foreground: Int, background: Int, styles: Set<String>) =
        screen.setAttribute(foreground, background, styles)

    override fun getCharAt(column: Int, row: Int) = scrollback.getCharAt(column, row)
    override fun getAttributesAt(column: Int, row: Int) = scrollback.getAttributesAt(column, row)
    override fun getLineAsString(row: Int): String {
        return if (row < height) {
            screen.getLineAsString(row)
        } else {
            scrollback.getLineAsString(row - height)
        }
    }
    override fun pushLine(line: List<Symbol>) = scrollback.pushLine(line)

    override fun insertEmptyLine() {
        TODO("Not yet implemented")
    }

    override fun writeText(text: String) {
        for (char in text) {
            if (char == '\n') {
                val scrolledLine = screen.newLine()
                if (scrolledLine != null) {
                    scrollback.pushLine(scrolledLine)
                }
            } else {
                screen.writeChar(char)
                val (x, y) = screen.getCursorPosition()
                if (x < width - 1) {
                    screen.moveCursorRight(1)
                } else {
                    val scrolledLine = screen.newLine()
                    if (scrolledLine != null) {
                        scrollback.pushLine(scrolledLine)
                    }
                }
            }
        }
    }

    override fun insertText(text: String) {
        TODO("Not yet implemented")
    }

}
