package ru.nnsh.woof_connect.common.dog_profile

import java.net.URI

data class WfcDogProfileBase(
    val ownerId: WfcOwnerId = WfcOwnerId.None,
    val dogId: WfcDogId = WfcDogId.None,
    val name: String = "",
    val breed: String? = null,
    val age: Int = -1,
    val weight: Float = Float.NaN,
    val description: String? = null,
    val photoUrl: URI? = null,
) {
    fun isEmpty() = this == None

    companion object {
        private val None = WfcDogProfileBase()
    }
}