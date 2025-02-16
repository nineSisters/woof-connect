package ru.nnsh.woof_connect.repository

import ru.nanashi.ru.nnsh.cor.impl.worker
import ru.nnsh.woof_connect.WfcChain
import ru.nnsh.woof_connect.common.helpers.fail
import ru.nnsh.woof_connect.common.repository.DbDogIdRequest
import ru.nnsh.woof_connect.common.repository.IDbResponse
import ru.nnsh.woof_connect.onRunning

fun WfcChain.prepareReadDog() = worker {
    title = "Подготовка ответа чтения"
    onRunning()
    doWork { dogProfileDone = dogProfileRead }
}

fun WfcChain.readDog() = worker {
    title = "Чтение собаки из БД"
    onRunning()
    doWork {
        val request = DbDogIdRequest(dogProfileRequest.dogId)
        val result = dogProfileRepository.readDog(request)
        if (result is IDbResponse.Err) {
            fail(result.err)
        } else {
            dogProfileRead = result.data
        }
    }
}
