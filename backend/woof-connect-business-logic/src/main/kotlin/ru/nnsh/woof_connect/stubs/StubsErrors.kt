package ru.nnsh.woof_connect.stubs

import org.slf4j.Logger
import ru.nanashi.ru.nnsh.cor.impl.worker
import ru.nnsh.woof_connect.WfcChain
import ru.nnsh.woof_connect.common.WfcError
import ru.nnsh.woof_connect.common.WfcState
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileStub
import ru.nnsh.woof_connect.logger.doWithLogging

internal fun WfcChain.stubValidationBadName(
    title: String,
) = worker {
    this.title = title
    on { stub == WfcDogProfileStub.BAD_NAME && state == WfcState.RUNNING }
    val logger: Logger = configuration.loggerFactory.getLogger("stubValidationBadName")
    doWork {
        logger.doWithLogging(requestId.toString()) {
            state = WfcState.FAILING
            error = WfcError(
                code = WfcError.CODE_VALIDATION,
                field = "name",
                group = "validation",
                message = "Wrong name"
            )
        }
    }
}

internal fun WfcChain.stubValidationBadAge(
    title: String,
) = worker {
    this.title = title
    on { stub == WfcDogProfileStub.BAD_AGE && state == WfcState.RUNNING }
    val logger: Logger = configuration.loggerFactory.getLogger("stubValidationBadAge")
    doWork {
        logger.doWithLogging(requestId.toString()) {
            state = WfcState.FAILING
            error = WfcError(
                code = WfcError.CODE_VALIDATION,
                field = "name",
                group = "validation",
                message = "Wrong age"
            )
        }
    }
}

internal fun WfcChain.stubBdErrorNoSuchUser(
    title: String,
) = worker {
    this.title = title
    on { stub == WfcDogProfileStub.NOT_FOUND && state == WfcState.RUNNING }
    val logger: Logger = configuration.loggerFactory.getLogger("stubBdErrorNoSuchUser")
    doWork {
        logger.doWithLogging(requestId.toString()) {
            state = WfcState.FAILING
            error = WfcError(
                code = WfcError.CODE_DB,
                field = "name",
                group = "db",
                message = "No such user"
            )
        }
    }
}

internal fun WfcChain.stubNoSuchStubsCase(
    title: String,
) = worker {
    this.title = title
    val logger: Logger = configuration.loggerFactory.getLogger("stubNoSuchStubsCase")
    on { state == WfcState.RUNNING }
    doWork {
        logger.doWithLogging(requestId.toString()) {
            state = WfcState.FAILING
            error = WfcError(
                code = WfcError.CODE_SYSTEM,
                field = "stub",
                group = "logic",
                message = "No such stub"
            )
        }
    }
}


internal fun WfcChain.stubValidationOtherUserDog(
    title: String,
) = worker {
    this.title = title
    on { stub == WfcDogProfileStub.BAD_OWNER_ID && state == WfcState.RUNNING }
    val logger: Logger = configuration.loggerFactory.getLogger("stubValidationOtherUserDog")
    doWork {
        logger.doWithLogging(requestId.toString()) {
            state = WfcState.FAILING
            error = WfcError(
                code = WfcError.CODE_VALIDATION,
                field = "ownerId",
                group = "validation",
                message = "Wrong user"
            )
        }
    }
}

internal fun WfcChain.stubBdErrorNoSuchDog(
    title: String,
) = worker {
    this.title = title
    on { stub == WfcDogProfileStub.NOT_FOUND && state == WfcState.RUNNING }
    val logger: Logger = configuration.loggerFactory.getLogger("stubBdErrorNoSuchDog")
    doWork {
        logger.doWithLogging(requestId.toString()) {
            state = WfcState.FAILING
            error = WfcError(
                code = WfcError.CODE_DB,
                field = "dogId",
                group = "database",
                message = "Dog not found"
            )
        }
    }
}