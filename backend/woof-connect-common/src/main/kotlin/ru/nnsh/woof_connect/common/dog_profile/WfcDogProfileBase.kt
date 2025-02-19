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

        fun WfcDogProfileBase.mergeWith(other: WfcDogProfileBase): WfcDogProfileBase {
            return WfcDogProfileBase(
                ownerId = if (this.ownerId != WfcOwnerId.None) this.ownerId else other.ownerId,
                dogId = if (this.dogId != WfcDogId.None) this.dogId else other.dogId,
                name = this.name.ifEmpty { other.name },
                breed = this.breed ?: other.breed,
                age = if (this.age != -1) this.age else other.age,
                weight = if (!this.weight.isNaN()) this.weight else other.weight,
                description = this.description ?: other.description,
                photoUrl = this.photoUrl ?: other.photoUrl
            )
        }
    }
}
