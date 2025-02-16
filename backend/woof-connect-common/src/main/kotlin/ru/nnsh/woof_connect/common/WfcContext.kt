package ru.nnsh.woof_connect.common

import kotlinx.datetime.Instant
import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileCommand
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileStub
import ru.nnsh.woof_connect.common.ws.WfcWsSession

class WfcContext {
    var command: WfcDogProfileCommand = WfcDogProfileCommand.NONE
    var state: WfcState = WfcState.NONE
    var error: WfcError? = null

    var workMode: WfcWorkMode = WfcWorkMode.PROD
    var stub: WfcDogProfileStub = WfcDogProfileStub.NONE

    var requestId: WfcRequestId = WfcRequestId.None
    var timeStart: Instant = Instant.DISTANT_PAST

    var dogProfileRequest: WfcDogProfileBase = WfcDogProfileBase()
    var dogProfileResponse: WfcDogProfileBase = WfcDogProfileBase()

    var allDogsResponse: List<WfcDogId> = emptyList()

    var wsSession: WfcWsSession? = null

    var dogProfileRead: WfcDogProfileBase = WfcDogProfileBase() // have read from repository
    var dogProfilePrepare: WfcDogProfileBase = WfcDogProfileBase() // preparing to save to repository

    companion object {
        inline operator fun invoke(block: WfcContext.() -> Unit) = WfcContext().apply(block)
    }
}
