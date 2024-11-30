package com.mrdip.devfestkmpdemo

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import java.awt.Robot
import java.awt.event.KeyEvent

class MacOSKeyboardSimulator {
    // Custom CoreGraphics JNA interface
    interface CoreGraphicsLibrary : Library {
        companion object {
            val INSTANCE: CoreGraphicsLibrary = Native.load("CoreGraphics", CoreGraphicsLibrary::class.java)
        }

        // Native method declarations for key event creation and posting
        fun CGEventCreateKeyboardEvent(
            source: Pointer?,
            virtualKey: Short,
            keyDown: Boolean
        ): Pointer?

        fun CGEventSetFlags(
            event: Pointer?,
            flags: Long
        )

        fun CGEventPost(
            tapLocation: Long,
            event: Pointer?
        )

        fun CGEventRelease(
            event: Pointer?
        )
    }

    // Carbon library for key codes
    interface CarbonLibrary : Library {
        companion object {
            val INSTANCE: CarbonLibrary = Native.load("Carbon", CarbonLibrary::class.java)
        }

        // Key code constants
//        val kVK_ANSI_C = 0x08
//        val kVK_Command: Int
//            get() = 0x37
//        val kVK_Tab = 0x30
//        val kVK_Alt = 0x3A
    }

    // Event tap locations
    object EventTapLocations {
        const val kCGHIDEventTap = 0L
        const val kCGSessionEventTap = 1L
        const val kCGAnnotatedSessionEventTap = 2L
    }

    // Event flags (modifier keys)
    object EventFlags {
        const val kCGEventFlagMaskCommand = 1L shl 20
        const val kCGEventFlagMaskAlternate = 1L shl 19
    }

    /**
     * Simulate a key press using CoreGraphics
     * @param virtualKeyCode The virtual key code to simulate
     * @param modifierFlags Optional modifier flags (e.g., Command key)
     */
    fun simulateKeyPress(
        virtualKeyCode: Int,
        modifierFlags: Long = 0L
    ) {
        val cg = CoreGraphicsLibrary.INSTANCE

        // Create key down event
        val keyDownEvent = cg.CGEventCreateKeyboardEvent(
            null,
            virtualKeyCode.toShort(),
            true
        )

        // Set modifier flags if any
        if (modifierFlags != 0L) {
            cg.CGEventSetFlags(keyDownEvent, modifierFlags)
        }

        // Post key down event
        cg.CGEventPost(EventTapLocations.kCGHIDEventTap, keyDownEvent)

        // Create key up event
        val keyUpEvent = cg.CGEventCreateKeyboardEvent(
            null,
            virtualKeyCode.toShort(),
            false
        )

        // Set modifier flags for key up if needed
        if (modifierFlags != 0L) {
            cg.CGEventSetFlags(keyUpEvent, modifierFlags)
        }

        // Post key up event
        cg.CGEventPost(EventTapLocations.kCGHIDEventTap, keyUpEvent)

        // Release events to prevent memory leaks
        cg.CGEventRelease(keyDownEvent)
        cg.CGEventRelease(keyUpEvent)
    }

    // Convenience methods for common key combinations
    fun simulateCommandC() {
        // Simulate Command + C
//        simulateKeyPress(
//            CarbonLibrary.INSTANCE.kVK_ANSI_C,
//            EventFlags.kCGEventFlagMaskCommand
//        )
    }

    fun simulateAltTab() {
        // Simulate Alt + Tab
//        simulateKeyPress(
//            //CarbonLibrary.INSTANCE.kVK_Tab,
//            //EventFlags.kCGEventFlagMaskAlternate
//        )
    }

    companion object {
        // Factory method to ensure this is only used on macOS
        fun createForMacOS(): MacOSKeyboardSimulator {
            return if (System.getProperty("os.name").contains("Mac", ignoreCase = true)) {
                MacOSKeyboardSimulator()
            } else {
                throw UnsupportedOperationException("This class is only for macOS")
            }
        }
    }
}

class KeyboardSimulatorMacOS {
    // Quartz Core Graphics interface via JNA
    interface CoreGraphics : Library {
        fun CGEventCreateKeyboardEvent(
            source: Pointer?, keyCode: Short, keyDown: Boolean
        ): Pointer

        fun CGEventPost(
            tap: Int, event: Pointer
        )

        fun CFRelease(event: Pointer)
    }

    companion object {
        private const val TAP_EVENT = 0 // For global events
        private val cg = Native.load("CoreGraphics", CoreGraphics::class.java)

        // Keycode mapping: Use macOS-specific codes
        private const val RIGHT_ARROW_KEYCODE: Short = 124
        private const val LEFT_ARROW_KEYCODE: Short = 123
    }

    fun simulateRightArrowKeyPress() {
        pressKey(RIGHT_ARROW_KEYCODE)
    }

    fun simulateLeftArrowKeyPress() {
        pressKey(LEFT_ARROW_KEYCODE)
    }

    private fun pressKey(keyCode: Short) {
        val keyDownEvent = cg.CGEventCreateKeyboardEvent(null, keyCode, true)
        val keyUpEvent = cg.CGEventCreateKeyboardEvent(null, keyCode, false)

        // Post events globally
        cg.CGEventPost(TAP_EVENT, keyDownEvent)
        cg.CGEventPost(TAP_EVENT, keyUpEvent)

        // Release resources
        cg.CFRelease(keyDownEvent)
        cg.CFRelease(keyUpEvent)
    }
}

class KeyboardSimulator {
    private val robot = Robot()

    fun simulateRightArrowKeyPress() {
        robot.keyPress(KeyEvent.VK_RIGHT)
        robot.keyRelease(KeyEvent.VK_RIGHT)
    }

    fun simulateLeftArrowKeyPress() {
        robot.keyPress(KeyEvent.VK_LEFT)
        robot.keyRelease(KeyEvent.VK_LEFT)
    }
}