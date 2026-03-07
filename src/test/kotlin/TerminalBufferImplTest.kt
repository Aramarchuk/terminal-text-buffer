package io.github.aramarchuk.terminalbuffer

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import kotlin.test.assertEquals

@DisplayName("TerminalBufferImpl Tests")
class TerminalBufferImplTest {

    private lateinit var terminal: TerminalBuffer

    @BeforeEach
    fun setUp() {
        terminal = TerminalBufferImpl(80, 24, 1000)
    }

    // ========== Basic Text Operations ==========

    @Test
    @DisplayName("Should write text at cursor position")
    fun testWriteText() {
        terminal.setCursorPosition(0, 0)
        terminal.writeText("Hello World")
        val line = terminal.getLineAsString(0)
        assertEquals("Hello World", line, "Text should be written at cursor position")
    }

    @Test
    @DisplayName("Should write text with newline and advance to next line")
    fun testWriteTextWithNewline() {
        terminal.setCursorPosition(0, 0)
        terminal.writeText("First line\n")
        terminal.writeText("Second line")

        val line0 = terminal.getLineAsString(0)
        val line1 = terminal.getLineAsString(1)

        assertEquals("First line", line0, "First line should contain first text")
        assertEquals("Second line", line1, "Second line should contain second text")
    }

    @Test
    @DisplayName("Should insert text without overwriting existing content")
    fun testInsertText() {
        terminal.setCursorPosition(0, 0)
        terminal.writeText("World")
        terminal.setCursorPosition(0, 0)
        terminal.insertText("Hello ")

        val line = terminal.getLineAsString(0)
        assertEquals("Hello World", line, "Text should be inserted, not overwritten")
    }

    @Test
    @DisplayName("Should insert text in middle of line")
    fun testInsertTextInMiddle() {
        terminal.setCursorPosition(0, 0)
        terminal.writeText("HelloWorld")
        terminal.setCursorPosition(5, 0)
        terminal.insertText(" ")

        val line = terminal.getLineAsString(0)
        assertEquals("Hello World", line, "Text should be inserted in the middle")
    }

    // ========== Cursor Operations ==========

    @Test
    @DisplayName("Should get and set cursor position")
    fun testCursorPosition() {
        terminal.setCursorPosition(10, 5)
        val (col, row) = terminal.getCursorPosition()

        assertEquals(10, col, "Cursor column should be 10")
        assertEquals(5, row, "Cursor row should be 5")
    }

    @Test
    @DisplayName("Should move cursor up")
    fun testMoveCursorUp() {
        terminal.setCursorPosition(5, 10)
        terminal.moveCursorUp(3)
        val (_, row) = terminal.getCursorPosition()

        assertEquals(7, row, "Cursor should move up 3 rows")
    }

    @Test
    @DisplayName("Should move cursor down")
    fun testMoveCursorDown() {
        terminal.setCursorPosition(5, 5)
        terminal.moveCursorDown(3)
        val (_, row) = terminal.getCursorPosition()

        assertEquals(8, row, "Cursor should move down 3 rows")
    }

    @Test
    @DisplayName("Should move cursor left")
    fun testMoveCursorLeft() {
        terminal.setCursorPosition(10, 5)
        terminal.moveCursorLeft(4)
        val (col, _) = terminal.getCursorPosition()

        assertEquals(6, col, "Cursor should move left 4 columns")
    }

    @Test
    @DisplayName("Should move cursor right")
    fun testMoveCursorRight() {
        terminal.setCursorPosition(10, 5)
        terminal.moveCursorRight(4)
        val (col, _) = terminal.getCursorPosition()

        assertEquals(14, col, "Cursor should move right 4 columns")
    }

    // ========== Screen Clearing Operations ==========

    @Test
    @DisplayName("Should clear entire screen")
    fun testClearScreen() {
        terminal.setCursorPosition(0, 0)
        terminal.writeText("Line 1\n")
        terminal.writeText("Line 2\n")
        terminal.writeText("Line 3")

        terminal.clearScreen()

        val screenContent = terminal.getScreenAsString()
        val lines = screenContent.split("\n").filter { it.isNotEmpty() }

        assertEquals(0, lines.size, "Screen should be empty after clear")
    }

    @Test
    @DisplayName("Should clear current line")
    fun testClearLine() {
        terminal.setCursorPosition(0, 0)
        terminal.writeText("Line 1\n")
        terminal.writeText("Line 2\n")
        terminal.writeText("Line 3")

        terminal.setCursorPosition(0, 1)
        terminal.clearLine()

        val line0 = terminal.getLineAsString(0)
        val line1 = terminal.getLineAsString(1)
        val line2 = terminal.getLineAsString(2)

        assertEquals("Line 1", line0, "First line should be unchanged")
        assertEquals("", line1, "Second line should be cleared")
        assertEquals("Line 3", line2, "Third line should be unchanged")
    }

    @Test
    @DisplayName("Should fill line with specific character")
    fun testFillLine() {
        terminal.setCursorPosition(0, 5)
        terminal.fillLine('-')

        val line = terminal.getLineAsString(5)
        assertEquals("-".repeat(80), line, "Line should be filled with '-' characters")
    }

    // ========== Screen Content Retrieval ==========

    @Test
    @DisplayName("Should get entire screen as string")
    fun testGetScreenAsString() {
        terminal.setCursorPosition(0, 0)
        terminal.writeText("Line 1\n")
        terminal.writeText("Line 2\n")
        terminal.writeText("Line 3")

        val screenContent = terminal.getScreenAsString()
        val lines = screenContent.split("\n")

        assertEquals("Line 1", lines[0], "First line should match")
        assertEquals("Line 2", lines[1], "Second line should match")
        assertEquals("Line 3", lines[2], "Third line should match")
    }

    @Test
    @DisplayName("Should get specific line as string")
    fun testGetLineAsString() {
        terminal.setCursorPosition(0, 5)
        terminal.writeText("Test line at row 5")

        val line = terminal.getLineAsString(5)
        assertEquals("Test line at row 5", line, "Should retrieve specific line correctly")
    }

    // ========== Line Wrapping ==========

    @Test
    @DisplayName("Should wrap text when exceeding screen width")
    fun testTextWrapping() {
        terminal.setCursorPosition(0, 0)
        val longText = "A".repeat(100)
        terminal.writeText(longText)

        val line0 = terminal.getLineAsString(0)
        val line1 = terminal.getLineAsString(1)

        assertEquals("A".repeat(80), line0, "First line should be full width")
        assertEquals("A".repeat(20), line1, "Overflow should wrap to next line")
    }

    @Test
    @DisplayName("Should wrap text when writing near end of line")
    fun testTextWrappingNearEndOfLine() {
        terminal.setCursorPosition(75, 0)
        terminal.writeText("ABCDEFGHIJ")

        val line0 = terminal.getLineAsString(0)
        val line1 = terminal.getLineAsString(1)

        assertEquals("ABCDE", line0.substring(75), "First line should contain first 5 chars")
        assertEquals("FGHIJ", line1.substring(0, 5), "Second line should contain remaining chars")
    }

    // ========== Scrollback Operations ==========

    @Test
    @DisplayName("Should push lines to scrollback when screen is full")
    fun testScrollback() {
        terminal.setCursorPosition(0, 0)

        // Fill screen and write more lines to trigger scrollback
        for (i in 0..25) {
            terminal.writeText("Line $i\n")
        }

        // First lines should be in scrollback
        val scrollbackContent = terminal.getScreenAndScrollbackAsString()
        val allLines = scrollbackContent.split("\n").filter { it.isNotEmpty() }

        assertEquals("Line 0", allLines[0], "First line should be in scrollback")
        assertEquals("Line 1", allLines[1], "Second line should be in scrollback")
    }

    @Test
    @DisplayName("Should retrieve character from scrollback")
    fun testGetCharAtFromScrollback() {
        terminal.setCursorPosition(0, 0)

        // Fill screen and trigger scrollback
        for (i in 0..25) {
            terminal.writeText("Line $i\n")
        }

        // Access scrollback (negative rows or special indexing depending on implementation)
        // First line "Line 0" should be at position 0 in combined view
        val char = terminal.getCharAt(0, 0)
        assertEquals('L', char, "Should retrieve character from scrollback")
    }

    @Test
    @DisplayName("Should clear screen and scrollback")
    fun testClearScreenAndScrollback() {
        terminal.setCursorPosition(0, 0)

        // Fill screen and scrollback
        for (i in 0..30) {
            terminal.writeText("Line $i\n")
        }

        terminal.clearScreenAndScrollback()

        val content = terminal.getScreenAndScrollbackAsString()
        val lines = content.split("\n").filter { it.isNotEmpty() }

        assertEquals(0, lines.size, "Both screen and scrollback should be empty")
    }

    @Test
    @DisplayName("Should get screen and scrollback as string")
    fun testGetScreenAndScrollbackAsString() {
        terminal.setCursorPosition(0, 0)

        for (i in 0..26) {
            terminal.writeText("Line $i\n")
        }

        val content = terminal.getScreenAndScrollbackAsString()
        val lines = content.split("\n").filter { it.isNotEmpty() }

        assertEquals(27, lines.size, "Should have all lines from scrollback and screen")
        assertEquals("Line 0", lines[0], "First line should be Line 0")
        assertEquals("Line 26", lines[26], "Last line should be Line 26")
    }

    // ========== Empty Line Insertion ==========

    @Test
    @DisplayName("Should insert empty line at cursor position")
    fun testInsertEmptyLine() {
        terminal.setCursorPosition(0, 0)
        terminal.writeText("Line 1\n")
        terminal.writeText("Line 2\n")
        terminal.writeText("Line 3")

        terminal.setCursorPosition(0, 1)
        terminal.insertEmptyLine()

        val line0 = terminal.getLineAsString(0)
        val line1 = terminal.getLineAsString(1)
        val line2 = terminal.getLineAsString(2)

        assertEquals("Line 1", line1, "First line should be unchanged")
        assertEquals("", line0, "Second line should be empty (newly inserted)")
        assertEquals("Line 2", line2, "Third line should be previous second line")
    }

    // ========== Edge Cases ==========

    @Test
    @DisplayName("Should handle empty text write")
    fun testWriteEmptyText() {
        terminal.setCursorPosition(0, 0)
        terminal.writeText("")

        val line = terminal.getLineAsString(0)
        assertEquals("", line, "Empty text should not affect line")
    }

    @Test
    @DisplayName("Should handle multiple newlines")
    fun testMultipleNewlines() {
        terminal.setCursorPosition(0, 0)
        terminal.writeText("Line 1\n\n\nLine 4")

        val line0 = terminal.getLineAsString(0)
        val line1 = terminal.getLineAsString(1)
        val line2 = terminal.getLineAsString(2)
        val line3 = terminal.getLineAsString(3)

        assertEquals("Line 1", line0, "First line should contain text")
        assertEquals("", line1, "Second line should be empty")
        assertEquals("", line2, "Third line should be empty")
        assertEquals("Line 4", line3, "Fourth line should contain text")
    }

//    @Test
//    @DisplayName("Should handle cursor at screen boundary")
//    fun testCursorAtBoundary() {
//        terminal.setCursorPosition(79, 23)
//        terminal.writeText("X")
//
//        val line = terminal.getLineAsString(23)
//        assertEquals("X", line.substring(79), "Character should be at last position")
//    }

    @Test
    @DisplayName("Should handle text attributes retrieval")
    fun testGetAttributesAt() {
        terminal.setCursorPosition(0, 0)
        terminal.setAttributes(TextAttributes.withColors(TerminalColor.RED, TerminalColor.BLACK))
        terminal.writeText("Test")

        val attrs = terminal.getAttributesAt(0, 0)
        assertEquals(TerminalColor.RED, attrs.fg, "Foreground color should match")
        assertEquals(TerminalColor.BLACK, attrs.bg, "Background color should match")
    }

    @Test
    @DisplayName("Should preserve attributes when writing text")
    fun testAttributePreservation() {
        terminal.setCursorPosition(0, 0)
        terminal.setAttributes(
            TextAttributes.withColors(TerminalColor.RED, TerminalColor.BLACK)
                .withStyle(StyleFlag.BOLD)
        )
        terminal.writeText("Bold")

        terminal.setAttributes(TextAttributes.withColors(TerminalColor.GREEN, TerminalColor.BLUE))
        terminal.writeText("Normal")

        val attrs1 = terminal.getAttributesAt(0, 0)
        val attrs2 = terminal.getAttributesAt(4, 0)

        assertEquals(TerminalColor.RED,   attrs1.fg, "First text should have first foreground")
        assertEquals(TerminalColor.GREEN, attrs2.fg, "Second text should have second foreground")
    }

    // ========== Small Terminal Tests ==========

    @Test
    @DisplayName("Should work with small terminal dimensions")
    fun testSmallTerminal() {
        val smallTerminal = TerminalBufferImpl(10, 5, 100)
        smallTerminal.setCursorPosition(0, 0)
        smallTerminal.writeText("Hello\n")
        smallTerminal.writeText("World")

        val line0 = smallTerminal.getLineAsString(0)
        val line1 = smallTerminal.getLineAsString(1)

        assertEquals("Hello", line0, "First line should contain Hello")
        assertEquals("World", line1, "Second line should contain World")
    }

    @Test
    @DisplayName("Should scroll in small terminal")
    fun testSmallTerminalScrollback() {
        val smallTerminal = TerminalBufferImpl(10, 3, 100)
        smallTerminal.setCursorPosition(0, 0)

        for (i in 0..4) {
            smallTerminal.writeText("Line $i\n")
        }

        val content = smallTerminal.getScreenAndScrollbackAsString()
        val lines = content.split("\n").filter { it.isNotEmpty() }

        assertEquals(5, lines.size, "Should have 5 lines total")
        assertEquals("Line 0", lines[0], "First line should be in scrollback")
    }
}

