package ru.nnsh.woof_connect.repository

import ru.nanashi.ru.nnsh.cor.impl.worker
import ru.nnsh.woof_connect.WfcChain
import ru.nnsh.woof_connect.common.WfcState
import ru.nnsh.woof_connect.common.helpers.fail
import ru.nnsh.woof_connect.common.repository.DbOwnerIdRequest
import ru.nnsh.woof_connect.common.repository.IDbResponse
import ru.nnsh.woof_connect.onRunning

fun WfcChain.prepareListDogs() = worker {
    title = "Подготовка к поиску собак пользователя в БД"
    onRunning()
    doWork {
        dogProfilePrepare = dogProfileRequest
    }
}

fun WfcChain.listDogs() = worker {
    title = "Поиск собак пользователя в БД"
    onRunning()
    doWork {
        val request = DbOwnerIdRequest(dogProfilePrepare.ownerId)
        val result = dogProfileRepository.listDogs(request)
        if (result is IDbResponse.Err) {
            fail(result.err)
        } else {
            allDogsResponse = result.data
        }
        state = WfcState.FINISHING
    }
}
