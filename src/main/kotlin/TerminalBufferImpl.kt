package io.github.aramarchuk.terminalbuffer

class TerminalBufferImpl(
    val width: Int,
    val height: Int,
    val scrollbackSize: Int,
    val screen: Screen = ScreenImpl(width, height),
    val scrollback: Scrollback = ScrollbackImpl(scrollbackSize)
) : TerminalBuffer, ScreenPublic by screen, ScrollbackPublic by scrollback {
    override fun getScreenAndScrollbackAsString(): String {
        val sb = StringBuilder()
        val scrollbackLines = scrollback.getActualSize()
        for (row in scrollbackLines - 1 downTo 0) {
            sb.append(scrollback.getLineAsString(row))
            sb.append("\n")
        }
        for (row in height - 1 downTo 0) {
            sb.append(screen.getLineAsString(row))
            sb.append("\n")
        }
        return sb.toString()
    }

    override fun getAttributesAt(
        column: Int,
        row: Int
    ): TextAttributes {
        return if (row < height) {
            screen.getAttributesAt(column, row)
        }
        else {
            scrollback.getAttributesAt(column, row-height)
        }
    }

    override fun getLineAsString(row: Int): String {
        return if (row < height) {
            screen.getLineAsString(row)
        } else {
            scrollback.getLineAsString(row - height)
        }
    }

    override fun getCharAt(column: Int, row: Int): Char {
        return if (row < height) {
            screen.getCharAt(column, row)
        } else {
            scrollback.getCharAt(column, row - height)
        }
    }

    override fun isLineWrapped(row: Int): Boolean {
        return if (row < height) {
            screen.isLineWrapped(row)
        } else {
            scrollback.isLineWrapped(row - height)
        }
    }

    override fun insertEmptyLine() {
        screen.scrollDown()
    }

    override fun writeText(text: String) {
        for (char in text) {
            if (char == '\n') {
                if (screen.isAtBottomLine()) {
                    val scrolledLine = screen.scrollUp()
                    scrollback.pushLine(scrolledLine)
                }
                screen.moveCursorToNextLine(wrapped = false)
            } else {
                screen.writeChar(char)
                val x = screen.getCursorPosition().column
                if (x < width - 1) {
                    screen.moveCursorRight(1)
                } else {
                    if (screen.isAtBottomLine()) {
                        val scrolledLine = screen.scrollUp()
                        scrollback.pushLine(scrolledLine)
                    }
                    screen.moveCursorToNextLine(wrapped = true)
                }
            }
        }
    }

    override fun insertText(text: String) {
        for (char in text) {
            if (char == '\n') {
                if (screen.isAtBottomLine()) {
                    val scrolledLine = screen.scrollUp()
                    scrollback.pushLine(scrolledLine)
                }
                screen.moveCursorToNextLine(wrapped = false)
            } else {
                val overflow = screen.insertChar(char)
                val (x, _) = screen.getCursorPosition()

                if (x < width - 1) {
                    screen.moveCursorRight(1)
                } else {
                    if (screen.isAtBottomLine()) {
                        val scrolledLine = screen.scrollUp()
                        scrollback.pushLine(scrolledLine)
                    }
                    screen.moveCursorToNextLine(wrapped = true)
                }

                if (overflow != null) {
                    val savedPos = screen.getCursorPosition()
                    screen.setCursorPosition(0, savedPos.row)
                    screen.insertChar(overflow.char)
                    screen.setCursorPosition(savedPos.column, savedPos.row)
                }
            }
        }
    }

    override fun clearScreenAndScrollback() {
        screen.clearScreen()
        scrollback.clear()
    }
}
