package io.github.aramarchuk.terminalbuffer

fun main() {
    val terminal = TerminalBufferImpl(80, 10, 1000)
    terminal.writeText("Hello Terminal!\n")
    terminal.writeText("The newest string always has index 0\n")
    terminal.writeText("Since I'm ending each string here with the newline symbol, the line 0 will be empty if we check\n")
    println(terminal.getScreenAsString())
    println(terminal.getLineAsString(0))
    terminal.setAttributes(TextAttributes.default().withStyle(StyleFlag.BOLD))
    terminal.writeText("This text is considered BOLD. Let's check this\n")
    println("+++++++++++")
    println(terminal.getScreenAsString())
    println("+++++++++++")
    val isBold = terminal.getAttributesAt(0, 1).isBold
    println(isBold)

    terminal.setCursorPosition(0, 7)
    terminal.setAttributes(terminal.getAttributes().withStyles(StyleFlag.ITALIC))
    terminal.writeText("Since Screen is modifiable, let's modify it")
    println(terminal.getLineAsString(7))
    println(terminal.getAttributesAt(0, 7).isBold)
    println(terminal.getAttributesAt(0, 7).isItalic)
    terminal.setCursorPosition(0, 0)
    terminal.writeText("This line will become 0-index line in the scrollback\n")
    for (i in 0..8) terminal.writeText(i.toString() + '\n')
    println(terminal.getScreenAsString())
    println(terminal.getLineAsString(10))
}