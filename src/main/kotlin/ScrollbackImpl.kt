package io.github.aramarchuk.terminalbuffer

data class Symbol(val char: Char = ' ', val attr: TextAttributes)

class ScrollbackImpl(
    private var scrollbackSize: Int
) : Scrollback {
    private val content: ArrayDeque<ArrayDeque<Symbol>> = ArrayDeque()
    fun getScrollbackSize() = scrollbackSize

    override fun getCharAt(column: Int, row: Int): Char =
        content.getOrNull(row)
            ?.getOrNull(column)
            ?.char ?: error("Out of bounds")

    override fun getAttributesAt(
        column: Int,
        row: Int
    ): TextAttributes =
        content.getOrNull(row)
            ?.getOrNull(column)?.attr ?: error("Out of bounds")

    override fun getLineAsString(row: Int): String =
        content.getOrNull(row)
            ?.joinToString(separator = "") { it.char.toString() }
            ?.trimEnd()
            ?: error("Out of bounds")

    override fun pushLine(line: List<Symbol>) {
        // Add the new line to the end of scrollback
        content.addLast(ArrayDeque(line))

        // Remove oldest line if we exceed scrollback size
        if (content.size > scrollbackSize) {
            content.removeFirst()
        }
    }
}