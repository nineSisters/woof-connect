package ru.nnsh.woof_connect.common.models

import kotlinx.datetime.Instant
import ru.nnsh.woof_connect.common.models.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.models.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.models.dog_profile.WfcDogProfileCommand
import ru.nnsh.woof_connect.common.models.dog_profile.WfcDogProfileStub

class WfcContext {
    var command: WfcDogProfileCommand = WfcDogProfileCommand.NONE
    var state: WfcState = WfcState.NONE
    val error: WfcError? = null

    var workMode: WfcWorkMode = WfcWorkMode.PROD
    var stub: WfcDogProfileStub = WfcDogProfileStub.NONE

    var requestId: WfcRequestId = WfcRequestId.None
    var timeStart: Instant = Instant.DISTANT_PAST

    var dogProfileRequest: WfcDogProfileBase = WfcDogProfileBase()
    var dogProfileResponse: WfcDogProfileBase = WfcDogProfileBase()

    var allDogsResponse: List<WfcDogId> = emptyList()

    inline operator fun invoke(block: WfcContext.() -> Unit): WfcContext = apply {
        block()
    }
}
