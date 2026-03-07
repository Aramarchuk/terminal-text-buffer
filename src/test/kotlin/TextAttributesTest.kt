package io.github.aramarchuk.terminalbuffer

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class TextAttributesTest {

    @Test
    fun `default attributes have correct values`() {
        val attrs = TextAttributes.default()
        assertEquals(7, attrs.fg)
        assertEquals(0, attrs.bg)
        assertEquals(0, attrs.flags)
    }

    @Test
    fun `withColors creates attributes with custom colors`() {
        val attrs = TextAttributes.withColors(foreground = 10, background = 5)
        assertEquals(10, attrs.fg)
        assertEquals(5, attrs.bg)
    }

    @Test
    fun `withStyle adds single style flag`() {
        val attrs = TextAttributes.default()
            .withStyle(TextAttributes.BOLD)

        assertTrue(attrs.isBold)
        assertFalse(attrs.isItalic)
        assertEquals(TextAttributes.BOLD, attrs.flags)
    }

    @Test
    fun `withStyles adds multiple style flags`() {
        val attrs = TextAttributes.default()
            .withStyles(TextAttributes.BOLD, TextAttributes.ITALIC)

        assertTrue(attrs.isBold)
        assertTrue(attrs.isItalic)
        assertFalse(attrs.isUnderline)
        assertEquals(TextAttributes.BOLD or TextAttributes.ITALIC, attrs.flags)
    }

    @Test
    fun `withoutStyle removes style flag`() {
        val attrs = TextAttributes.default()
            .withStyles(TextAttributes.BOLD, TextAttributes.ITALIC)
            .withoutStyle(TextAttributes.ITALIC)

        assertTrue(attrs.isBold)
        assertFalse(attrs.isItalic)
        assertEquals(TextAttributes.BOLD, attrs.flags)
    }

    @Test
    fun `withoutStyles removes multiple style flags`() {
        val attrs = TextAttributes.default()
            .withStyles(TextAttributes.BOLD, TextAttributes.ITALIC, TextAttributes.UNDERLINE)
            .withoutStyles(TextAttributes.ITALIC, TextAttributes.UNDERLINE)

        assertTrue(attrs.isBold)
        assertFalse(attrs.isItalic)
        assertFalse(attrs.isUnderline)
        assertEquals(TextAttributes.BOLD, attrs.flags)
    }

    @Test
    fun `hasStyle checks for specific style`() {
        val attrs = TextAttributes.default()
            .withStyle(TextAttributes.BOLD)

        assertTrue(attrs.hasStyle(TextAttributes.BOLD))
        assertFalse(attrs.hasStyle(TextAttributes.ITALIC))
    }

    @Test
    fun `all style properties work correctly`() {
        var attrs = TextAttributes.default()

        assertFalse(attrs.isBold)
        assertFalse(attrs.isItalic)
        assertFalse(attrs.isUnderline)
        assertFalse(attrs.isStrikethrough)

        attrs = attrs.withStyles(
            TextAttributes.BOLD,
            TextAttributes.ITALIC,
            TextAttributes.UNDERLINE,

            TextAttributes.STRIKETHROUGH
        )

        assertTrue(attrs.isBold)
        assertTrue(attrs.isItalic)
        assertTrue(attrs.isUnderline)
        assertTrue(attrs.isStrikethrough)
    }

    @Test
    fun `withForeground changes foreground color`() {
        val attrs = TextAttributes.default()
            .withForeground(15)

        assertEquals(15, attrs.fg)
        assertEquals(0, attrs.bg)
    }

    @Test
    fun `withBackground changes background color`() {
        val attrs = TextAttributes.default()
            .withBackground(8)

        assertEquals(7, attrs.fg)
        assertEquals(8, attrs.bg)
    }

    @Test
    fun `withColors changes both colors`() {
        val attrs = TextAttributes.default()
            .withColors(foreground = 12, background = 3)

        assertEquals(12, attrs.fg)
        assertEquals(3, attrs.bg)
    }

    @Test
    fun `chaining multiple operations works correctly`() {
        val attrs = TextAttributes.default()
            .withForeground(10)
            .withBackground(5)
            .withStyle(TextAttributes.BOLD)
            .withStyle(TextAttributes.ITALIC)

        assertEquals(10, attrs.fg)
        assertEquals(5, attrs.bg)
        assertTrue(attrs.isBold)
        assertTrue(attrs.isItalic)
    }

    @Test
    fun `immutability - operations return new objects`() {
        val original = TextAttributes.default()
        val modified = original.withStyle(TextAttributes.BOLD)

        assertNotSame(original, modified)
        assertFalse(original.isBold)
        assertTrue(modified.isBold)
    }

    @Test
    fun `bit flags have correct values`() {
        assertEquals(0b00000001, TextAttributes.BOLD)
        assertEquals(0b00000010, TextAttributes.ITALIC)
        assertEquals(0b00000100, TextAttributes.UNDERLINE)
        assertEquals(0b00001000, TextAttributes.STRIKETHROUGH)
    }

    @Test
    fun `complex example - styled text with colors`() {
        val errorText = TextAttributes.withColors(foreground = 1, background = 0)
            .withStyles(TextAttributes.BOLD, TextAttributes.UNDERLINE)

        assertEquals(1, errorText.fg)
        assertEquals(0, errorText.bg)
        assertTrue(errorText.isBold)
        assertTrue(errorText.isUnderline)
        assertFalse(errorText.isItalic)

        val warningText = errorText
            .withForeground(3)
            .withoutStyle(TextAttributes.UNDERLINE)
            .withStyle(TextAttributes.ITALIC)

        assertEquals(3, warningText.fg)
        assertEquals(0, warningText.bg)
        assertTrue(warningText.isBold)
        assertFalse(warningText.isUnderline)
        assertTrue(warningText.isItalic)
    }
}

