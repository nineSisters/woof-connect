package ru.nnsh.woof_connect.repository

import ru.nanashi.ru.nnsh.cor.impl.worker
import ru.nnsh.woof_connect.WfcChain
import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase.Companion.mergeWith
import ru.nnsh.woof_connect.common.helpers.fail
import ru.nnsh.woof_connect.common.repository.DbDogProfileRequest
import ru.nnsh.woof_connect.common.repository.IDbResponse
import ru.nnsh.woof_connect.onRunning


fun WfcChain.prepareUpdateDog() = worker {
    title = "Подготовка собаки к обновлению в БД: мёрджим данные из запроса из БД"
    onRunning()
    doWork {
        dogProfilePrepare = dogProfileRequest
            .copy(dogId = WfcDogId.None)
            .mergeWith(dogProfileRead)
    }
}

fun WfcChain.updateDog() = worker {
    title = "Обновление собаки в БД"
    onRunning()
    doWork {
        val request = DbDogProfileRequest(dogProfilePrepare)
        val result = dogProfileRepository.updateDog(request)
        if (result is IDbResponse.Err) {
            fail(result.err)
        } else {
            dogProfileDone = result.data
        }
    }
}
