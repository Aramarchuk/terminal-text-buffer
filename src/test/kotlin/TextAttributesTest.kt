package io.github.aramarchuk.terminalbuffer

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

class TextAttributesTest {

    // ── Factory methods ───────────────────────────────────────────────────────

    @Test
    fun `default attributes have correct values`() {
        val attrs = TextAttributes.default()
        assertEquals(TerminalColor.WHITE, attrs.fg)
        assertEquals(TerminalColor.BLACK, attrs.bg)
        assertFalse(attrs.isBold)
        assertFalse(attrs.isItalic)
        assertFalse(attrs.isUnderline)
        assertFalse(attrs.isStrikethrough)
    }

    @Test
    fun `withColors factory creates attributes with custom colors`() {
        val attrs = TextAttributes.withColors(TerminalColor.RED, TerminalColor.BLUE)
        assertEquals(TerminalColor.RED,  attrs.fg)
        assertEquals(TerminalColor.BLUE, attrs.bg)
    }

    @Test
    fun `withColors factory with no args matches default fg and bg`() {
        val fromFactory = TextAttributes.withColors()
        val dflt = TextAttributes.default()
        assertEquals(dflt.fg, fromFactory.fg)
        assertEquals(dflt.bg, fromFactory.bg)
    }

    // ── Style flags ───────────────────────────────────────────────────────────

    @Test
    fun `withStyle adds single style flag`() {
        val attrs = TextAttributes.default().withStyle(StyleFlag.BOLD)
        assertTrue(attrs.isBold)
        assertFalse(attrs.isItalic)
    }

    @Test
    fun `withStyles adds multiple style flags`() {
        val attrs = TextAttributes.default()
            .withStyles(StyleFlag.BOLD, StyleFlag.ITALIC)
        assertTrue(attrs.isBold)
        assertTrue(attrs.isItalic)
        assertFalse(attrs.isUnderline)
    }

    @Test
    fun `withoutStyle removes style flag`() {
        val attrs = TextAttributes.default()
            .withStyles(StyleFlag.BOLD, StyleFlag.ITALIC)
            .withoutStyle(StyleFlag.ITALIC)
        assertTrue(attrs.isBold)
        assertFalse(attrs.isItalic)
    }

    @Test
    fun `withoutStyles removes multiple style flags`() {
        val attrs = TextAttributes.default()
            .withStyles(StyleFlag.BOLD, StyleFlag.ITALIC, StyleFlag.UNDERLINE)
            .withoutStyles(StyleFlag.ITALIC, StyleFlag.UNDERLINE)
        assertTrue(attrs.isBold)
        assertFalse(attrs.isItalic)
        assertFalse(attrs.isUnderline)
    }

    @Test
    fun `hasStyle checks for specific style`() {
        val attrs = TextAttributes.default().withStyle(StyleFlag.BOLD)
        assertTrue(attrs.hasStyle(StyleFlag.BOLD))
        assertFalse(attrs.hasStyle(StyleFlag.ITALIC))
    }

    @Test
    fun `all four style properties work correctly`() {
        val attrs = TextAttributes.default()
            .withStyles(StyleFlag.BOLD, StyleFlag.ITALIC, StyleFlag.UNDERLINE, StyleFlag.STRIKETHROUGH)
        assertTrue(attrs.isBold)
        assertTrue(attrs.isItalic)
        assertTrue(attrs.isUnderline)
        assertTrue(attrs.isStrikethrough)
    }

    @Test
    fun `withStyle with already-set flag is idempotent`() {
        val first  = TextAttributes.default().withStyle(StyleFlag.BOLD)
        val second = first.withStyle(StyleFlag.BOLD)
        assertTrue(second.isBold)
        assertEquals(first, second)
    }

    @Test
    fun `withoutStyle on flag that was never set leaves attrs unchanged`() {
        val attrs  = TextAttributes.default().withStyle(StyleFlag.BOLD)
        val result = attrs.withoutStyle(StyleFlag.ITALIC)
        assertTrue(result.isBold)
        assertFalse(result.isItalic)
        assertEquals(attrs, result)
    }

    @Test
    fun `withStyles with duplicate flags is idempotent`() {
        val attrs = TextAttributes.default().withStyles(StyleFlag.BOLD, StyleFlag.BOLD)
        assertTrue(attrs.isBold)
        assertFalse(attrs.isItalic)
    }

    @Test
    fun `withStyles with empty vararg leaves attrs unchanged`() {
        val attrs  = TextAttributes.default().withStyle(StyleFlag.BOLD)
        val result = attrs.withStyles()
        assertEquals(attrs, result)
    }

    @Test
    fun `withoutStyles with empty vararg leaves attrs unchanged`() {
        val attrs  = TextAttributes.default().withStyle(StyleFlag.BOLD)
        val result = attrs.withoutStyles()
        assertEquals(attrs, result)
    }

    // ── Color methods ─────────────────────────────────────────────────────────

    @Test
    fun `withForeground changes foreground color`() {
        val attrs = TextAttributes.default().withForeground(TerminalColor.BRIGHT_WHITE)
        assertEquals(TerminalColor.BRIGHT_WHITE, attrs.fg)
        assertEquals(TerminalColor.BLACK, attrs.bg)
    }

    @Test
    fun `withBackground changes background color`() {
        val attrs = TextAttributes.default().withBackground(TerminalColor.BRIGHT_BLACK)
        assertEquals(TerminalColor.WHITE,       attrs.fg)
        assertEquals(TerminalColor.BRIGHT_BLACK, attrs.bg)
    }

    @Test
    fun `withColors instance method changes both colors`() {
        val attrs = TextAttributes.default().withColors(TerminalColor.CYAN, TerminalColor.MAGENTA)
        assertEquals(TerminalColor.CYAN,    attrs.fg)
        assertEquals(TerminalColor.MAGENTA, attrs.bg)
    }

    // ── Preservation of attributes across mutations ───────────────────────────

    @Test
    fun `withForeground preserves style flags`() {
        val attrs = TextAttributes.default()
            .withStyle(StyleFlag.ITALIC)
            .withForeground(TerminalColor.BRIGHT_CYAN)
        assertTrue(attrs.isItalic)
        assertEquals(TerminalColor.BRIGHT_CYAN, attrs.fg)
    }

    @Test
    fun `withBackground preserves style flags`() {
        val attrs = TextAttributes.default()
            .withStyle(StyleFlag.UNDERLINE)
            .withBackground(TerminalColor.BLUE)
        assertTrue(attrs.isUnderline)
        assertEquals(TerminalColor.BLUE, attrs.bg)
    }

    @Test
    fun `withColors instance method preserves style flags`() {
        val attrs = TextAttributes.default()
            .withStyle(StyleFlag.BOLD)
            .withColors(TerminalColor.GREEN, TerminalColor.YELLOW)
        assertEquals(TerminalColor.GREEN,  attrs.fg)
        assertEquals(TerminalColor.YELLOW, attrs.bg)
        assertTrue(attrs.isBold)
    }

    // ── TerminalColor enum ────────────────────────────────────────────────────

    @Test
    fun `all 16 TerminalColor entries have unique codes 0 to 15`() {
        val codes = TerminalColor.entries.map { it.code }
        assertEquals(16, codes.size)
        assertEquals((0..15).toList(), codes.sorted())
    }

    @Test
    fun `TerminalColor fromCode round-trips for all valid codes`() {
        for (code in 0..15) {
            assertEquals(code, TerminalColor.fromCode(code).code)
        }
    }

    @Test
    fun `TerminalColor fromCode throws for invalid code`() {
        assertThrows<IllegalArgumentException> { TerminalColor.fromCode(-1) }
        assertThrows<IllegalArgumentException> { TerminalColor.fromCode(16) }
        assertThrows<IllegalArgumentException> { TerminalColor.fromCode(255) }
    }

    // ── Immutability ──────────────────────────────────────────────────────────

    @Test
    fun `operations return new objects`() {
        val original = TextAttributes.default()
        val modified = original.withStyle(StyleFlag.BOLD)
        assertNotSame(original, modified)
        assertFalse(original.isBold)
        assertTrue(modified.isBold)
    }

    // ── equals / hashCode ─────────────────────────────────────────────────────

    @Test
    fun `equals compares by value not reference`() {
        val a = TextAttributes.default().withStyle(StyleFlag.BOLD).withForeground(TerminalColor.RED)
        val b = TextAttributes.default().withStyle(StyleFlag.BOLD).withForeground(TerminalColor.RED)
        assertNotSame(a, b)
        assertEquals(a, b)
    }

    @Test
    fun `hashCode is consistent with equals`() {
        val a = TextAttributes.default().withStyle(StyleFlag.BOLD).withForeground(TerminalColor.RED)
        val b = TextAttributes.default().withStyle(StyleFlag.BOLD).withForeground(TerminalColor.RED)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `objects differing only in fg are not equal`() {
        val a = TextAttributes.default().withForeground(TerminalColor.RED)
        val b = TextAttributes.default().withForeground(TerminalColor.GREEN)
        assertNotEquals(a, b)
    }

    @Test
    fun `objects differing only in bg are not equal`() {
        val a = TextAttributes.default().withBackground(TerminalColor.RED)
        val b = TextAttributes.default().withBackground(TerminalColor.GREEN)
        assertNotEquals(a, b)
    }

    @Test
    fun `objects differing only in style flags are not equal`() {
        val a = TextAttributes.default().withStyle(StyleFlag.BOLD)
        val b = TextAttributes.default().withStyle(StyleFlag.ITALIC)
        assertNotEquals(a, b)
    }

    // ── toString ──────────────────────────────────────────────────────────────

    @Test
    fun `toString on default contains fg bg and NONE styles`() {
        val s = TextAttributes.default().toString()
        assertTrue(s.contains("WHITE"), "expected WHITE in: $s")
        assertTrue(s.contains("BLACK"), "expected BLACK in: $s")
        assertTrue(s.contains("NONE"),  "expected NONE styles in: $s")
    }

    @Test
    fun `toString lists active style names`() {
        val s = TextAttributes.default()
            .withStyles(StyleFlag.BOLD, StyleFlag.UNDERLINE)
            .toString()
        assertTrue(s.contains("BOLD"),      "expected BOLD in: $s")
        assertTrue(s.contains("UNDERLINE"), "expected UNDERLINE in: $s")
        assertFalse(s.contains("ITALIC"),   "unexpected ITALIC in: $s")
    }

    // ── Integration ───────────────────────────────────────────────────────────

    @Test
    fun `chaining multiple operations works correctly`() {
        val attrs = TextAttributes.default()
            .withForeground(TerminalColor.BRIGHT_GREEN)
            .withBackground(TerminalColor.BLUE)
            .withStyle(StyleFlag.BOLD)
            .withStyle(StyleFlag.ITALIC)
        assertEquals(TerminalColor.BRIGHT_GREEN, attrs.fg)
        assertEquals(TerminalColor.BLUE,         attrs.bg)
        assertTrue(attrs.isBold)
        assertTrue(attrs.isItalic)
    }

    @Test
    fun `complex example - styled error and warning text`() {
        val errorText = TextAttributes.withColors(TerminalColor.RED, TerminalColor.BLACK)
            .withStyles(StyleFlag.BOLD, StyleFlag.UNDERLINE)
        assertEquals(TerminalColor.RED, errorText.fg)
        assertTrue(errorText.isBold)
        assertTrue(errorText.isUnderline)
        assertFalse(errorText.isItalic)

        val warningText = errorText
            .withForeground(TerminalColor.YELLOW)
            .withoutStyle(StyleFlag.UNDERLINE)
            .withStyle(StyleFlag.ITALIC)
        assertEquals(TerminalColor.YELLOW, warningText.fg)
        assertTrue(warningText.isBold)
        assertFalse(warningText.isUnderline)
        assertTrue(warningText.isItalic)
    }
}
