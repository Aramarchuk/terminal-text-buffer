package io.github.aramarchuk.terminalbuffer

data class Symbol(val char: Char = ' ', val attr: TextAttributes)

data class Line(val symbols: MutableList<Symbol>, var wrapped: Boolean = false)

class ScrollbackImpl(
    private var scrollbackSize: Int
) : Scrollback {
    private val content: ArrayDeque<Line> = ArrayDeque()

    override fun clear() {
        content.clear()
    }

    override fun getActualSize() = content.size

    override fun getCharAt(column: Int, row: Int): Char =
        content.getOrNull(row)
            ?.symbols?.getOrNull(column)
            ?.char ?: error("Out of bounds")

    override fun getAttributesAt(
        column: Int,
        row: Int
    ): TextAttributes =
        content.getOrNull(row)
            ?.symbols?.getOrNull(column)?.attr ?: error("Out of bounds")

    override fun getLineAsString(row: Int): String =
        content.getOrNull(row)
            ?.symbols?.joinToString(separator = "") { it.char.toString() }
            ?.trimEnd()
            ?: error("Out of bounds")

    override fun isLineWrapped(row: Int): Boolean =
        content.getOrNull(row)?.wrapped ?: error("Out of bounds")

    override fun pushLine(line: Line) {
        content.addLast(line)

        if (content.size > scrollbackSize) {
            content.removeFirst()
        }
    }
}