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
        val (x, y) = terminal.getCursorPosition()
        assertEquals(5, x, "Cursor column should be 5")
        assertEquals(5, y, "Cursor row should be 5")
    }

//    @Test
//    @DisplayName("Should clear screen properly")
//    fun testClearScreen() {
//        screen.setCursorPosition(0, 0)
//        screen.writeText("Some text")
//        screen.clearScreen()
//        val content = screen.getScreenAsString()
//        // After clearing, the screen should not have meaningful content
//        assertEquals(true, content.isNotEmpty(), "Screen should exist after clear")
//    }
}

