package ru.nnsh.woof_connect.common.models.dog_profile

@JvmInline
value class WfcOwnerId(val id: Long) {
    companion object {
        val None = WfcOwnerId(0)
    }
}
