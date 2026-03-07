package io.github.aramarchuk.terminalbuffer

/**
 * Text attributes for terminal characters.
 *
 * @property fg Foreground color (0-255)
 * @property bg Background color (0-255)
 * @property flags Bit flags for text styles (internal representation)
 */
data class TextAttributes(
    val fg: Int = 7,
    val bg: Int = 0,
    val flags: Int = 0
) {
    companion object {
        // Bit flags for text styles
        const val BOLD = 1 shl 0       // 0b00000001
        const val ITALIC = 1 shl 1     // 0b00000010
        const val UNDERLINE = 1 shl 2  // 0b00000100
        const val STRIKETHROUGH = 1 shl 3 // 0b00001000

        /**
         * Create TextAttributes with default colors and no styles.
         */
        fun default() = TextAttributes()

        /**
         * Create TextAttributes with specified colors.
         */
        fun withColors(foreground: Int = 7, background: Int = 0) =
            TextAttributes(fg = foreground, bg = background)
    }

    /**
     * Check if a specific style is set.
     */
    fun hasStyle(style: Int): Boolean = (flags and style) != 0

    /**
     * Check if bold style is set.
     */
    val isBold: Boolean get() = hasStyle(BOLD)

    /**
     * Check if italic style is set.
     */
    val isItalic: Boolean get() = hasStyle(ITALIC)

    /**
     * Check if underline style is set.
     */
    val isUnderline: Boolean get() = hasStyle(UNDERLINE)

    /**
     * Check if strikethrough style is set.
     */
    val isStrikethrough: Boolean get() = hasStyle(STRIKETHROUGH)

    /**
     * Add a style flag to the current attributes.
     */
    fun withStyle(style: Int): TextAttributes =
        copy(flags = flags or style)

    /**
     * Add multiple style flags to the current attributes.
     */
    fun withStyles(vararg styles: Int): TextAttributes =
        copy(flags = styles.fold(flags) { acc, style -> acc or style })

    /**
     * Remove a style flag from the current attributes.
     */
    fun withoutStyle(style: Int): TextAttributes =
        copy(flags = flags and style.inv())

    /**
     * Remove multiple style flags from the current attributes.
     */
    fun withoutStyles(vararg styles: Int): TextAttributes =
        copy(flags = styles.fold(flags) { acc, style -> acc and style.inv() })

    /**
     * Change foreground color.
     */
    fun withForeground(color: Int): TextAttributes =
        copy(fg = color)

    /**
     * Change background color.
     */
    fun withBackground(color: Int): TextAttributes =
        copy(bg = color)

    /**
     * Change both colors.
     */
    fun withColors(foreground: Int, background: Int): TextAttributes =
        copy(fg = foreground, bg = background)
}
