package io.github.aramarchuk.terminalbuffer

/**
 * Standard 16-color terminal palette (ANSI colors 0–15).
 *
 * The first 8 are the normal colors, the second 8 are their bright variants.
 */
enum class TerminalColor(val code: Int) {
    BLACK(0),
    RED(1),
    GREEN(2),
    YELLOW(3),
    BLUE(4),
    MAGENTA(5),
    CYAN(6),
    WHITE(7),
    BRIGHT_BLACK(8),
    BRIGHT_RED(9),
    BRIGHT_GREEN(10),
    BRIGHT_YELLOW(11),
    BRIGHT_BLUE(12),
    BRIGHT_MAGENTA(13),
    BRIGHT_CYAN(14),
    BRIGHT_WHITE(15);

    companion object {
        fun fromCode(code: Int): TerminalColor =
            entries.firstOrNull { it.code == code }
                ?: throw IllegalArgumentException("Unknown terminal color code: $code. Must be 0–15.")
    }
}

