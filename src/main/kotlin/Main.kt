package io.github.aramarchuk.terminalbuffer

fun main() {
    val terminal = TerminalBufferImpl(80, 24, 1000)
    terminal.writeText("Hello Terminal!")
    println(terminal.getScreenAsString())
}