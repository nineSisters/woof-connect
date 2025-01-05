@file:OptIn(ExperimentalUuidApi::class)

package ru.nnsh.woof_connect.common.models

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@JvmInline
value class WfcRequestId(val id: Uuid) {
    @OptIn(ExperimentalUuidApi::class)
    companion object {
        val None = WfcRequestId(Uuid.NIL)
    }
}
