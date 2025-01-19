@file:OptIn(ExperimentalUuidApi::class)

package ru.nnsh.woof_connect.common

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@JvmInline
value class WfcRequestId(private val id: Uuid) {
    @OptIn(ExperimentalUuidApi::class)
    companion object {
        val None = WfcRequestId(Uuid.NIL)
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun toString() = id.toString()
}
