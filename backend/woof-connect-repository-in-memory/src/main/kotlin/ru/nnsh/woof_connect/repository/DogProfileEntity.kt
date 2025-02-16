package ru.nnsh.woof_connect.repository

import java.net.URI
import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.dog_profile.WfcOwnerId

internal class DogProfileEntity private constructor(
    private val ownerId: Long,
    private val dogId: Long,
    private val name: String,
    private val breed: String?,
    private val age: Int,
    private val weight: Float,
    private val description: String?,
    private val photoUrl: String?,
) {
    constructor(model: WfcDogProfileBase) : this(
        ownerId = model.ownerId.id,
        dogId = model.dogId.id,
        name = model.name,
        breed = model.breed,
        age = model.age,
        weight = model.weight,
        description = model.description,
        photoUrl = model.photoUrl?.toString()
    )

    fun toInternal() = WfcDogProfileBase(
        ownerId = WfcOwnerId(ownerId),
        dogId = WfcDogId(dogId),
        name = name,
        breed = breed,
        age = age,
        weight = weight,
        description = description,
        photoUrl = photoUrl?.let { URI.create(it) }
    )
}
