package ru.nnsh.woof_connect.mappers

import ru.nnsh.woof_connect.api.v1.models.*
import ru.nnsh.woof_connect.common.models.WfcContext
import ru.nnsh.woof_connect.common.models.WfcError
import ru.nnsh.woof_connect.common.models.WfcState
import ru.nnsh.woof_connect.common.models.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.models.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.models.dog_profile.WfcDogProfileCommand
import ru.nnsh.woof_connect.common.models.dog_profile.WfcOwnerId

fun WfcContext.toTransportResponse(): BaseResponse = when (val cmd = command) {
    WfcDogProfileCommand.CREATE -> toTransportCreate()
    WfcDogProfileCommand.DELETE -> toTransportDelete()
    WfcDogProfileCommand.READ -> toTransportRead()
    WfcDogProfileCommand.UPDATE -> toTransportUpdate()
    WfcDogProfileCommand.READ_ALL_DOGS -> toTransportReadAllDogs()
    WfcDogProfileCommand.NONE -> error("Improper command $cmd")
}

private fun WfcContext.toTransportCreate() = DogProfileCreateResponse(
    isSuccess = state == WfcState.FINISHING,
    error = error.toTransport(),
    dogId = dogProfileResponse.dogId.takeIf { it != WfcDogId.None }?.toTransport()
)

private fun WfcContext.toTransportRead() = DogProfileReadResponse(
    isSuccess = state == WfcState.FINISHING,
    error = error.toTransport(),
    dogProfile = dogProfileResponse.toTransportDogProfile()
)

private fun WfcContext.toTransportDelete() = DogProfileDeleteResponse(
    isSuccess = state == WfcState.FINISHING,
    error = error.toTransport(),
)

private fun WfcContext.toTransportUpdate() = DogProfileUpdateResponse(
    isSuccess = state == WfcState.FINISHING,
    error = error.toTransport(),
    dogId = dogProfileResponse.dogId.takeIf { it != WfcDogId.None }?.toTransport()
)


private fun WfcContext.toTransportReadAllDogs() = UserDogIdsResponse(
    isSuccess = state == WfcState.FINISHING,
    error = error.toTransport(),
    dogIds = allDogsResponse.map(WfcDogId::toTransport)
)

private fun WfcDogProfileBase.toTransportDogProfile() = DogProfileBase(
    ownerId = ownerId.toTransport(),
    dogId = dogId.toTransport(),
    name = name,
    breed = breed,
    age = age,
    weight = weight,
    description = description,
    photoUrl = photoUrl
)

private fun WfcOwnerId.toTransport() = UserId(id)

private fun WfcError?.toTransport() = this?.run { BaseError(code, field, group, message) }

private fun WfcDogId.toTransport() = DogId(id)
