package ru.nnsh.woof_connect.repository

import ru.nanashi.ru.nnsh.cor.impl.worker
import ru.nnsh.woof_connect.WfcChain
import ru.nnsh.woof_connect.common.WfcError
import ru.nnsh.woof_connect.common.WfcWorkMode
import ru.nnsh.woof_connect.common.helpers.fail
import ru.nnsh.woof_connect.common.repository.IDogProfileRepository

fun WfcChain.initRepository() = worker {
    title = "Инициализация репозитория, соответствующего режиму работы"
    doWork {
        dogProfileRepository = when (workMode) {
            WfcWorkMode.PROD -> configuration.prodRepository
            WfcWorkMode.TEST -> configuration.testRepository
            WfcWorkMode.STUB -> configuration.stubRepository
        }
        if (dogProfileRepository == IDogProfileRepository.NONE && workMode != WfcWorkMode.STUB) {
            fail(
                code = WfcError.CODE_SYSTEM,
                field = "dogProfileRepository",
                message = "dbNotConfigured",
                cause = IllegalStateException("No repository for $workMode")
            )
        }
    }
}
