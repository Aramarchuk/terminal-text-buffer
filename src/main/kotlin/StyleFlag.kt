package io.github.aramarchuk.terminalbuffer
/**
 * Text style flags for terminal characters.
 *
 * Each entry corresponds to a single bit in the internal flags field of [TextAttributes].
 */
enum class StyleFlag(internal val bit: Int) {
    BOLD(1 shl 0),
    ITALIC(1 shl 1),
    UNDERLINE(1 shl 2),
    STRIKETHROUGH(1 shl 3)
}
