package ru.nnsh.woof_connect.mappers

import ru.nnsh.woof_connect.api.v1.models.BaseRequest
import ru.nnsh.woof_connect.api.v1.models.DogId
import ru.nnsh.woof_connect.api.v1.models.DogProfileBase
import ru.nnsh.woof_connect.api.v1.models.DogProfileCreateRequest
import ru.nnsh.woof_connect.api.v1.models.DogProfileDebug
import ru.nnsh.woof_connect.api.v1.models.DogProfileDeleteRequest
import ru.nnsh.woof_connect.api.v1.models.DogProfileReadRequest
import ru.nnsh.woof_connect.api.v1.models.DogProfileRequestDebugMode
import ru.nnsh.woof_connect.api.v1.models.DogProfileRequestDebugStubs
import ru.nnsh.woof_connect.api.v1.models.DogProfileUpdateRequest
import ru.nnsh.woof_connect.api.v1.models.UserDogIdsRequest
import ru.nnsh.woof_connect.api.v1.models.UserId
import ru.nnsh.woof_connect.common.models.WfcContext
import ru.nnsh.woof_connect.common.models.WfcWorkMode
import ru.nnsh.woof_connect.common.models.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.models.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.models.dog_profile.WfcDogProfileCommand
import ru.nnsh.woof_connect.common.models.dog_profile.WfcDogProfileStub
import ru.nnsh.woof_connect.common.models.dog_profile.WfcOwnerId

fun WfcContext.fromTransport(request: BaseRequest) = apply {
    when (request) {
        is DogProfileCreateRequest -> fromTransport(request)
        is DogProfileDeleteRequest -> fromTransport(request)
        is DogProfileReadRequest -> fromTransport(request)
        is DogProfileUpdateRequest -> fromTransport(request)
        is UserDogIdsRequest -> fromTransport(request)
    }
}

private fun WfcContext.fromTransport(request: DogProfileCreateRequest) {
    command = WfcDogProfileCommand.CREATE
    dogProfileRequest = request.dogProfile.toInternal()
    workMode = request.debug.toWorkMode()
    stub = request.debug.toStub()
}

private fun WfcContext.fromTransport(request: DogProfileDeleteRequest) {
    command = WfcDogProfileCommand.DELETE
    dogProfileRequest = WfcDogProfileBase(
        dogId = request.dogId.toInternal()
    )
    workMode = request.debug.toWorkMode()
    stub = request.debug.toStub()
}
private fun WfcContext.fromTransport(request: DogProfileReadRequest) {
    command = WfcDogProfileCommand.READ
    dogProfileRequest = WfcDogProfileBase(
        dogId = request.dogId.toInternal()
    )
    workMode = request.debug.toWorkMode()
    stub = request.debug.toStub()
}

private fun WfcContext.fromTransport(request: UserDogIdsRequest) {
    command = WfcDogProfileCommand.READ_ALL_DOGS
    dogProfileRequest = WfcDogProfileBase(
        ownerId = request.userId.toInternal()
    )
    workMode = request.debug.toWorkMode()
    stub = request.debug.toStub()
}

private fun WfcContext.fromTransport(request: DogProfileUpdateRequest) {
    command = WfcDogProfileCommand.UPDATE
    dogProfileRequest = request.dogProfile.toInternal()
    workMode = request.debug.toWorkMode()
    stub = request.debug.toStub()
}

private fun DogProfileBase.toInternal() = WfcDogProfileBase(
    ownerId = ownerId.toInternal(),
    dogId = dogId.toInternal(),
    name = name,
    breed = breed,
    age = age ?: -1,
    weight = weight ?: Float.NaN,
    description = description,
    photoUrl = photoUrl
)

private fun UserId.toInternal() = WfcOwnerId(userId)
private fun DogId.toInternal() = WfcDogId(dogId)
private fun DogProfileDebug?.toWorkMode() = when(this?.mode) {
    null, DogProfileRequestDebugMode.PROD -> WfcWorkMode.PROD
    DogProfileRequestDebugMode.TEST -> WfcWorkMode.TEST
    DogProfileRequestDebugMode.STUB -> WfcWorkMode.STUB
}

private fun DogProfileDebug?.toStub() = when (this?.stub) {
    null -> WfcDogProfileStub.NONE
    DogProfileRequestDebugStubs.SUCCESS -> WfcDogProfileStub.SUCCESS
    DogProfileRequestDebugStubs.NOT_FOUND -> WfcDogProfileStub.NOT_FOUND
    DogProfileRequestDebugStubs.BAD_DOG_ID -> WfcDogProfileStub.BAD_DOG_ID
    DogProfileRequestDebugStubs.BAD_OWNER_ID -> WfcDogProfileStub.BAD_OWNER_ID
    DogProfileRequestDebugStubs.BAD_NAME -> WfcDogProfileStub.BAD_NAME
    DogProfileRequestDebugStubs.BAD_AGE -> WfcDogProfileStub.BAD_AGE
}