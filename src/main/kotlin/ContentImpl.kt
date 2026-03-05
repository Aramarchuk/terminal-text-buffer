package io.github.aramarchuk.terminalbuffer

import kotlin.toString

data class Symbol(val char: Char?, val attr: TextAttributes)

class ContentImpl(
    private var scrollbackSize: Int = 1000,
    private val content: ArrayDeque<ArrayDeque<Symbol>> = ArrayDeque()
) : Content {
    fun getScrollbackSize() = scrollbackSize

    override fun setScrollbackSize(maxLines: Int) {
        content.dropLast(maxOf(0, scrollbackSize - maxLines))
        scrollbackSize = maxLines
    }

    override fun getCharAt(column: Int, row: Int): Char? =
        content.getOrNull(row)
            ?.getOrNull(column)
            ?.char

    override fun getAttributesAt(
        column: Int,
        row: Int
    ): TextAttributes? =
        content.getOrNull(row)
            ?.getOrNull(column)
            ?.attr

    override fun getLineAsString(row: Int): String =
        content.getOrNull(row)
            ?.joinToString(separator = "") { it.char?.toString() ?: " " }
            ?: ""

    override fun writeChar(x: Int, y: Int, symbol: Symbol) {
        while (content.size <= y) {
            content.addLast(ArrayDeque())
        }
        val line = content[y]
        while (line.size <= x) {
            line.addLast(Symbol(null, TextAttributes()))
        }
        line[x] = symbol
    }
}