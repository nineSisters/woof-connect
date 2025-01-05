package ru.nnsh.woof_connect.common.models.stubs

import java.net.URI
import ru.nnsh.woof_connect.common.models.WfcContext
import ru.nnsh.woof_connect.common.models.WfcState
import ru.nnsh.woof_connect.common.models.WfcWorkMode
import ru.nnsh.woof_connect.common.models.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.models.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.models.dog_profile.WfcDogProfileStub
import ru.nnsh.woof_connect.common.models.dog_profile.WfcOwnerId

fun WfcContext.processAsStub() {
    if (workMode != WfcWorkMode.STUB) return
    when (stub) {
        WfcDogProfileStub.SUCCESS, WfcDogProfileStub.NONE -> processSuccessStub()
        else -> processExceptionStub()
    }
}

private fun WfcContext.processSuccessStub() {
    state = WfcState.RUNNING
    dogProfileResponse = stubDog
    allDogsResponse = listOf(WfcDogId(1), WfcDogId(2))
}

private fun WfcContext.processExceptionStub() {
    error("Stub error")
}

val stubDog = WfcDogProfileBase(
    WfcOwnerId(11),
    WfcDogId(11),
    "Sharik",
    "Mongrel",
    3,
    22.5f,
    "Happy dog",
    URI.create("https://upload.wikimedia.org/wikipedia/commons/d/d5/Retriever_in_water.jpg")
)