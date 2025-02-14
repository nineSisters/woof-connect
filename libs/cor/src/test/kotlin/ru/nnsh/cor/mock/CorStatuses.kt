package ru.nnsh.cor.mock

sealed interface CorStatuses {
    data object None: CorStatuses
    data object Running: CorStatuses
    data object Failing: CorStatuses
}