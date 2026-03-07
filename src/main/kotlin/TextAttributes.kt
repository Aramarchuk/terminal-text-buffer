package io.github.aramarchuk.terminalbuffer

/**
 * Text attributes for terminal characters.
 *
 * Instances can only be created through [default] and [withColors] factory
 * functions, and modified through the provided `with*` / `without*` methods.
 *
 * Colors are expressed as [TerminalColor] enum values (standard 16-color palette).
 * Styles are expressed as [StyleFlag] enum values — no raw bit manipulation is exposed.
 *
 * @property fg Foreground color
 * @property bg Background color
 */
class TextAttributes private constructor(
    val fg: TerminalColor,
    val bg: TerminalColor,
    private val flags: Int
) {
    companion object {
        /**
         * Create [TextAttributes] with default colors (fg=WHITE, bg=BLACK) and no styles.
         */
        fun default() = TextAttributes(TerminalColor.WHITE, TerminalColor.BLACK, 0)

        /**
         * Create [TextAttributes] with specified colors and no styles.
         */
        fun withColors(
            foreground: TerminalColor = TerminalColor.WHITE,
            background: TerminalColor = TerminalColor.BLACK
        ) = TextAttributes(foreground, background, 0)
    }

    // ── Style queries ─────────────────────────────────────────────────────────

    /** Returns `true` if the given [style] flag is set. */
    fun hasStyle(style: StyleFlag): Boolean = (flags and style.bit) != 0

    val isBold: Boolean          get() = hasStyle(StyleFlag.BOLD)
    val isItalic: Boolean        get() = hasStyle(StyleFlag.ITALIC)
    val isUnderline: Boolean     get() = hasStyle(StyleFlag.UNDERLINE)
    val isStrikethrough: Boolean get() = hasStyle(StyleFlag.STRIKETHROUGH)

    // ── Style mutations ───────────────────────────────────────────────────────

    /** Return a copy with [style] added. */
    fun withStyle(style: StyleFlag) =
        TextAttributes(fg, bg, flags or style.bit)

    /** Return a copy with all given [styles] added. */
    fun withStyles(vararg styles: StyleFlag) =
        TextAttributes(fg, bg, styles.fold(flags) { acc, s -> acc or s.bit })

    /** Return a copy with [style] removed. */
    fun withoutStyle(style: StyleFlag) =
        TextAttributes(fg, bg, flags and style.bit.inv())

    /** Return a copy with all given [styles] removed. */
    fun withoutStyles(vararg styles: StyleFlag) =
        TextAttributes(fg, bg, styles.fold(flags) { acc, s -> acc and s.bit.inv() })

    // ── Color mutations ───────────────────────────────────────────────────────

    /** Return a copy with the foreground color changed. */
    fun withForeground(color: TerminalColor) = TextAttributes(color, bg, flags)

    /** Return a copy with the background color changed. */
    fun withBackground(color: TerminalColor) = TextAttributes(fg, color, flags)

    /** Return a copy with both colors changed. */
    fun withColors(foreground: TerminalColor, background: TerminalColor) =
        TextAttributes(foreground, background, flags)

    // ── equals / hashCode / toString ─────────────────────────────────────────

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TextAttributes) return false
        return fg == other.fg && bg == other.bg && flags == other.flags
    }

    override fun hashCode(): Int {
        var result = fg.hashCode()
        result = 31 * result + bg.hashCode()
        result = 31 * result + flags
        return result
    }

    override fun toString(): String {
        val styles = buildList {
            if (isBold)          add("BOLD")
            if (isItalic)        add("ITALIC")
            if (isUnderline)     add("UNDERLINE")
            if (isStrikethrough) add("STRIKETHROUGH")
        }.joinToString("|").ifEmpty { "NONE" }
        return "TextAttributes(fg=$fg, bg=$bg, styles=$styles)"
    }
}
