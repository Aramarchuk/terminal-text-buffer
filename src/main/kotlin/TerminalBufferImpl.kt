package io.github.aramarchuk.terminalbuffer

class TerminalBufferImpl : TerminalBuffer, Screen by ScreenImpl(), Content by ScreenImpl()