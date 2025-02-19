package ru.nnsh.woof_connect.repository

import ru.nanashi.ru.nnsh.cor.impl.worker
import ru.nnsh.woof_connect.WfcChain
import ru.nnsh.woof_connect.common.helpers.fail
import ru.nnsh.woof_connect.common.repository.DbDogIdRequest
import ru.nnsh.woof_connect.common.repository.IDbResponse
import ru.nnsh.woof_connect.onRunning


fun WfcChain.prepareDeleteDog() = worker {
    title = "Подготовка собаки к удалению из БД"
    onRunning()
    doWork {
        dogProfilePrepare = dogProfileRequest
    }
}

fun WfcChain.deleteDog() = worker {
    title = "Удаление собаки из БД по ID"
    onRunning()
    doWork {
        val request = DbDogIdRequest(dogProfilePrepare.dogId)
        val result = dogProfileRepository.deleteDog(request)
        if (result is IDbResponse.Err) {
            fail(result.err)
            dogProfileDone = dogProfileRead
        } else {
            dogProfileDone = result.data
        }
    }
}
