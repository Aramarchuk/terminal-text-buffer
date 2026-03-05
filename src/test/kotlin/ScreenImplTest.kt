package io.github.aramarchuk.terminalbuffer

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import kotlin.test.assertEquals

@DisplayName("ScreenImpl Tests")
class ScreenImplTest {

    private lateinit var screen: Screen

    @BeforeEach
    fun setUp() {
        screen = ScreenImpl()
        screen.setWindowSize(80, 24)
    }

    @Test
    @DisplayName("Should write text on screen")
    fun testWriteText() {
        screen.setCursorPosition(0, 0)
        screen.writeText("Hello")
        val content = screen.getScreenAsString()
        assertEquals(true, content.contains("Hello"), "Screen should contain written text")
    }

    @Test
    @DisplayName("Should handle cursor movement")
    fun testCursorPosition() {
        screen.setCursorPosition(5, 5)
        val (x, y) = screen.getCursorPosition()
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

