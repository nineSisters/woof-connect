package ru.nnsh.woof_connect.stubs

import org.slf4j.Logger
import ru.nanashi.ru.nnsh.cor.impl.worker
import ru.nnsh.woof_connect.WfcChain
import ru.nnsh.woof_connect.common.WfcState
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase.Companion.mergeWith
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileStub
import ru.nnsh.woof_connect.logger.doWithLogging

internal fun WfcChain.stubUpdateSuccess(
    title: String,
) = worker {
    this.title = title
    on { stub == WfcDogProfileStub.SUCCESS && state == WfcState.RUNNING }
    val logger: Logger = configuration.loggerFactory.getLogger("stubUpdateSuccess")
    doWork {
        logger.doWithLogging(requestId.toString()) {
            state = WfcState.FINISHING
            dogProfileResponse = dogProfileRequest.mergeWith(dogProfileResponse)
        }
    }
}
