# Terminal Text Buffer

A small Kotlin library that models a terminal screen with scrollback, cursor movement, text editing, and per-cell text attributes.

## Quick Start

```bash
./gradlew build
./gradlew run
./gradlew test
```

## What To Instantiate

Most users only need `TerminalBufferImpl`:

```kotlin
val terminal = TerminalBufferImpl(
    width = 80,
    height = 24,
    scrollbackSize = 1000
)
```

## Key Classes

- `TerminalBufferImpl`: main entry point; combines screen and scrollback behavior.
- `TextAttributes`: foreground/background colors and style flags (bold, italic, etc.).
- `TerminalColor`, `StyleFlag`: enums/helpers used to build `TextAttributes`.
- `ScreenImpl`, `ScrollbackImpl`: internal implementation details used by `TerminalBufferImpl`.

## Available Functionality

- Write and insert text: `writeText`, `insertText`.
- Move cursor: `setCursorPosition`, `moveCursorUp/Down/Left/Right`, `getCursorPosition`.
- Edit lines: `clearLine`, `fillLine`, `insertEmptyLine`, `clearScreen`, `clearScreenAndScrollback`.
- Read content: `getCharAt`, `getLineAsString`, `getScreenAsString`, `getScreenAndScrollbackAsString`.
- Work with style: `setAttributes`, `getAttributes`, `getAttributesAt`.
- Check wrapping: `isLineWrapped`.

## Indexing Rules

- Screen and scrollback use newest-first indexing: row `0` is the newest line.
- For the screen, row `height - 1` is the oldest visible line.
- String dumps (`getScreenAsString`, `getScreenAndScrollbackAsString`) are returned oldest-to-newest.

## Minimal Example

```kotlin
val terminal = TerminalBufferImpl(10, 3, 100)
terminal.writeText("Hello\n")
terminal.writeText("World")

println(terminal.getLineAsString(0)) // World (newest row)
println(terminal.getScreenAsString()) // oldest -> newest
```

## Architecture Notes
#### Buffer Management
I decided to split buffer management between Screen and Scrollback, since they have different requirements.

Scrollback is large and immutable in practice. The most common operations are adding lines to the beginning and removing them from the end, so these operations should be efficient. The most suitable data structure for this is a ring buffer (circular buffer). However, since this is not an algorithmic task, Kotlin’s ArrayDeque works well enough because it provides O(1) addFirst and removeLast operations.

The Screen, on the other hand, is much smaller, and modifications can happen at any position. Because of that, fast random access is more important, so a MutableList is sufficient here.

These implementation details should not be visible to the user. Access to both structures is provided through a unified interface, and InterfaceBufferImpl decides which underlying structure to use.

#### SetWindowSize
At first I assumed that I needed a function to configure the window and scrollback after the buffer had been initialized. However, this approach introduces many questionable design decisions. I eventually decided that TerminalBuffer should not exist without Screen and Scrollback, so their parameters must be known at initialization time.

#### Delegation problem
Initially I tried to split the large TerminalBuffer interface into smaller parts. However, this quickly led to a lot of boilerplate code that simply forwarded method calls without adding any logic.

The solution was to use Kotlin delegation. It allows me to keep the composition-based design while avoiding boilerplate and without exposing internal methods to the user.

#### Listeners
Some terminals, such as tmux, allow multiple screens to display and control the same underlying content. My implementation does not currently support this behavior. However, since Screen and Scrollback are already separated, supporting it would mostly require adding the ability to register and deregister Screens inside TerminalBuffer.

#### Cursor
At first it seems natural to model the Cursor as a separate object. In practice, however, it depends entirely on the Screen: it only exists within a screen and each screen can have only one cursor. Because of that, it makes more sense to treat the cursor as a part of the Screen rather than as an independent component.

## Drawbacks
#### Mutability
For the sake of a unified interface, in my implementation the Scrollback is represented as MutableLists, just like the Screen. This is not an ideal solution, but I believe it can be improved later, possibly by using Kotlin’s in/out variance mechanisms.

#### Enums
As far as I know, enums in the JVM are implemented as objects, so storing them may require more memory than primitive values. Because of that, I'm not entirely sure how much memory I actually save by representing symbol styles as a set of bit flags instead of enums. At least now it's save and incapsulated.

#### Scrolling
I decided that the user should not be able to actually "scroll" the screen itself. Allowing this would introduce several ambiguities. For example, what exactly is the screen when the user has scrolled up? Should it still be modifiable?

Instead, all scrollback content remains accessible through the getLineAsString function, which uses the same interface as access to the screen.