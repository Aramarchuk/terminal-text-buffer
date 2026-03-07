package io.github.aramarchuk.terminalbuffer

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import kotlin.test.assertEquals

@DisplayName("ScreenImpl Tests")
class ScreenImplTest {

    private lateinit var terminal: TerminalBuffer

    @BeforeEach
    fun setUp() {
        terminal = TerminalBufferImpl(24, 10, 1000)
    }

    @Test
    @DisplayName("Should write text on screen")
    fun testWriteText() {
        terminal.setCursorPosition(0, 0)
        terminal.writeText("Hello\n")
        terminal.writeText("World\n")
        val content = terminal.getLineAsString(1)
        assertEquals("World", content, "Screen should contain written text")
    }

    @Test
    @DisplayName("Should handle cursor movement")
    fun testCursorPosition() {
        terminal.setCursorPosition(5, 5)
        val pos = terminal.getCursorPosition()
        assertEquals(5, pos.column, "Cursor column should be 5")
        assertEquals(5, pos.row, "Cursor row should be 5")
    }

    @Test
    @DisplayName("Should clear screen properly")
    fun testClearScreen() {
        terminal.setCursorPosition(0, 0)
        terminal.writeText("Some text")
        terminal.clearScreen()
        val content = terminal.getScreenAsString()
        val expectedLines = List(10) { "" }.joinToString("\n") + "\n"
        assertEquals(expectedLines, content, "Screen should contain only spaces (empty lines)")
    }

    @Test
    @DisplayName("Should insert text and shift existing content right")
    fun testInsertText() {
        terminal.setCursorPosition(0, 0)
        terminal.writeText("Hello")
        terminal.setCursorPosition(0, 0)
        terminal.insertText(">> ")
        val content = terminal.getLineAsString(0)
        assertEquals(">> Hello", content, "Text should be inserted and existing content shifted")
    }

    @Test
    @DisplayName("Should insert text in the middle of line")
    fun testInsertTextMiddle() {
        terminal.setCursorPosition(0, 0)
        terminal.writeText("HelloWorld")
        terminal.setCursorPosition(5, 0)
        terminal.insertText(" ")
        val content = terminal.getLineAsString(0)
        assertEquals("Hello World", content, "Text should be inserted in the middle")
    }

    @Test
    @DisplayName("Should handle line wrapping when writing text")
    fun testLineWrapping() {
        terminal.setCursorPosition(0, 0)
        terminal.writeText("A".repeat(30))
        val line0 = terminal.getLineAsString(0)
        val line1 = terminal.getLineAsString(1)
        assertEquals("A".repeat(24), line0, "First line should be full")
        assertEquals("A".repeat(6), line1, "Overflow should wrap to next line")
    }

    @Test
    @DisplayName("Should scroll content to scrollback when screen is full")
    fun testScrollToScrollback() {
        terminal.setCursorPosition(0, 0)
        for (i in 0..11) {
            terminal.writeText("Line $i\n")
        }
        val line8 = terminal.getLineAsString(8)
        assertEquals("Line 11", line8, "Line 8 on screen should contain Line 11")

        val fullContent = terminal.getScreenAndScrollbackAsString()
        val allLines = fullContent.split("\n").filter { it.isNotEmpty() }
        assertEquals(12, allLines.size, "Should have 12 lines total (3 in scrollback + 9 on screen)")
        assertEquals("Line 0", allLines[0], "First scrollback line should be Line 0")
        assertEquals("Line 1", allLines[1], "Second scrollback line should be Line 1")
        assertEquals("Line 2", allLines[2], "Third scrollback line should be Line 2")
        assertEquals("Line 3", allLines[3], "First screen line should be Line 3")
    }
}

