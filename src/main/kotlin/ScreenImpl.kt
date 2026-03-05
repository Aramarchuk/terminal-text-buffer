package io.github.aramarchuk.terminalbuffer

data class TextAttributes(
    var fg: Int = 7,
    var bg: Int = 0,
    var flags: Int = 0
)

data class CursorState(var x: Int, var y: Int, val attr: TextAttributes = TextAttributes())

class ScreenImpl(
    private var width: Int = 80,
    private var height: Int = 24,
    private val cursorState: CursorState = CursorState(0, 0),
    private val contentImpl: ContentImpl = ContentImpl()
) : Screen, Content by contentImpl {
    override fun setWindowSize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }
    fun getWindowSize() = Pair(width, height)

    override fun insertEmptyLine() {
        TODO("Not yet implemented")
    }

    override fun clearScreen() {
        TODO("Not yet implemented")
    }

    override fun clearScreenAndScrollback() {
        TODO("Not yet implemented")
    }

    override fun writeText(text: String) {
        for (char in text) {
            writeChar(cursorState.x, cursorState.y, Symbol(char, cursorState.attr))
            moveCursorRight(1)
        }
    }

    override fun insertText(text: String) {
        TODO("Not yet implemented")
    }

    override fun fillLine(character: Char) {
        TODO("Not yet implemented")
    }

    override fun clearLine() {
        TODO("Not yet implemented")
    }

    override fun getCursorPosition(): Pair<Int, Int> {
        return Pair(cursorState.x, cursorState.y)
    }

    override fun setCursorPosition(column: Int, row: Int) {
        cursorState.x = column
        cursorState.y = row
    }

    override fun moveCursorUp(cells: Int) {
        TODO("Not yet implemented")
    }

    override fun moveCursorDown(cells: Int) {
        TODO("Not yet implemented")
    }

    override fun moveCursorLeft(cells: Int) {
        TODO("Not yet implemented")
    }

    override fun moveCursorRight(cells: Int) {
        cursorState.y += (cells + cursorState.x) / width
        cursorState.x = (cursorState.x + cells) % width
    }

    override fun getScreenAsString(): String {
        val sb = StringBuilder()
        for (row in 0 until height) {
            sb.append(getLineAsString(row))
            sb.append("\n")
        }
        return sb.toString()
    }

    override fun getScreenAndScrollbackAsString(): String {
        TODO("Not yet implemented")
    }

    override fun setAttribute(
        foreground: Int,
        background: Int,
        styles: Set<String>
    ) {
        TODO("Not yet implemented")
    }
}