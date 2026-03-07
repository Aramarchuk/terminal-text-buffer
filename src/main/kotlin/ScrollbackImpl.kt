package io.github.aramarchuk.terminalbuffer

data class Symbol(val char: Char = ' ', val attr: TextAttributes)

data class Line(val symbols: MutableList<Symbol>, var wrapped: Boolean = false)

class ScrollbackImpl(
    private var scrollbackSize: Int
) : Scrollback {
    private val content: ArrayDeque<Line> = ArrayDeque()

    /** Maps public scrollback row (0 = newest) to internal deque order (0 = oldest). */
    private fun toInternalRow(publicRow: Int): Int {
        if (publicRow !in 0 until content.size) {
            error("Out of bounds")
        }
        return content.size - 1 - publicRow
    }

    override fun clear() {
        content.clear()
    }

    override fun getActualSize() = content.size

    override fun getCharAt(column: Int, row: Int): Char {
        val internalRow = toInternalRow(row)
        return content.getOrNull(internalRow)
            ?.symbols?.getOrNull(column)
            ?.char ?: error("Out of bounds")
    }

    override fun getAttributesAt(
        column: Int,
        row: Int
    ): TextAttributes {
        val internalRow = toInternalRow(row)
        return content.getOrNull(internalRow)
            ?.symbols?.getOrNull(column)?.attr ?: error("Out of bounds")
    }

    override fun getLineAsString(row: Int): String {
        val internalRow = toInternalRow(row)
        return content.getOrNull(internalRow)
            ?.symbols?.joinToString(separator = "") { it.char.toString() }
            ?.trimEnd()
            ?: error("Out of bounds")
    }

    override fun isLineWrapped(row: Int): Boolean {
        return content.getOrNull(toInternalRow(row))?.wrapped ?: error("Out of bounds")
    }

    override fun pushLine(line: Line) {
        content.addLast(line)

        if (content.size > scrollbackSize) {
            content.removeFirst()
        }
    }
}