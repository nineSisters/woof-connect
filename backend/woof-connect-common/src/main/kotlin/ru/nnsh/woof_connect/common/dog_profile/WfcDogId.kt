package ru.nnsh.woof_connect.common.dog_profile

@JvmInline
value class WfcDogId(val id: Long) {
    constructor(id: Int) : this(id.toLong())
    companion object {
        val None = WfcDogId(0)
    }
}
