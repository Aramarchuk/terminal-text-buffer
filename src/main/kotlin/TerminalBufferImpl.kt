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
        for (row in 0 until scrollbackLines) {
            sb.append(scrollback.getLineAsString(row))
            sb.append("\n")
        }
        sb.append(screen.getScreenAsString())
        return sb.toString()
    }

    override fun getLineAsString(row: Int): String {
        return if (row < height) {
            screen.getLineAsString(row)
        } else {
            scrollback.getLineAsString(row - height)
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
                screen.moveCursorToNextLine()
            } else {
                screen.writeChar(char)
                val (x, _) = screen.getCursorPosition()
                if (x < width - 1) {
                    screen.moveCursorRight(1)
                } else {
                    if (screen.isAtBottomLine()) {
                        val scrolledLine = screen.scrollUp()
                        scrollback.pushLine(scrolledLine)
                    }
                    screen.moveCursorToNextLine()
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
                screen.moveCursorToNextLine()
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
                    screen.moveCursorToNextLine()
                }

                if (overflow != null) {
                    val savedPos = screen.getCursorPosition()
                    screen.setCursorPosition(0, savedPos.second)
                    screen.insertChar(overflow.char)
                    screen.setCursorPosition(savedPos.first, savedPos.second)
                }
            }
        }
    }

    override fun clearScreenAndScrollback() {
        screen.clearScreen()
        scrollback.clear()
    }

}
