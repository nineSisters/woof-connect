package ru.nnsh.cor.mock

import java.util.concurrent.atomic.AtomicReference

data class MockContext(
    var status: CorStatuses,
    var int: Int = 0,
) {
    private val aText = AtomicReference("initText")

    var text: String
        get() = aText.get()
        set(value) = aText.set(value)
}