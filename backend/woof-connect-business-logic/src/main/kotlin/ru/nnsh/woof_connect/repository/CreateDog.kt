package ru.nnsh.woof_connect.repository

import ru.nanashi.ru.nnsh.cor.impl.worker
import ru.nnsh.woof_connect.WfcChain
import ru.nnsh.woof_connect.common.dog_profile.WfcOwnerId
import ru.nnsh.woof_connect.common.helpers.fail
import ru.nnsh.woof_connect.common.repository.DbDogProfileRequest
import ru.nnsh.woof_connect.common.repository.IDbResponse
import ru.nnsh.woof_connect.onRunning


fun WfcChain.prepareCreateDog() = worker {
    title = "Подготовка собаки к сохранению в БД"
    onRunning()
    doWork {
        dogProfilePrepare = dogProfileRequest.copy(
            // TODO
            ownerId = WfcOwnerId.TheOne
        )
    }
}

fun WfcChain.createDog() = worker {
    title = "Сохранение собаки в БД"
    onRunning()
    doWork {
        val request = DbDogProfileRequest(dogProfilePrepare)
        val result = dogProfileRepository.createDog(request)
        if (result is IDbResponse.Err) {
            fail(result.err)
        } else {
            dogProfileDone = result.data
        }
    }
}
