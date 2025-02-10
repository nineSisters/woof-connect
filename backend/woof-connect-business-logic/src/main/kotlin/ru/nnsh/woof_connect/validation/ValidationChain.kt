package ru.nnsh.woof_connect.validation

import ru.nanashi.ru.nnsh.cor.dsl.ICorChainDsl
import ru.nanashi.ru.nnsh.cor.impl.chain
import ru.nanashi.ru.nnsh.cor.impl.worker
import ru.nnsh.woof_connect.WfcChain
import ru.nnsh.woof_connect.WfcCorConfiguration
import ru.nnsh.woof_connect.common.WfcContext
import ru.nnsh.woof_connect.common.WfcError.Companion.CODE_VALIDATION
import ru.nnsh.woof_connect.common.WfcState
import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileCommand
import ru.nnsh.woof_connect.common.dog_profile.WfcOwnerId
import ru.nnsh.woof_connect.common.helpers.fail
import ru.nnsh.woof_connect.operation

internal fun WfcChain.validationChain() = validation {
    operation("Регистрация собаки", WfcDogProfileCommand.CREATE) {
        trimDogName()
        validateDogName()
        validateDogHasWeight()
        validateDogHasAge()
        validateHasUserId()
        validateDescription()
    }
    operation("Удаление собаки", WfcDogProfileCommand.DELETE) {
        validateHasUserId()
        validateHasDogId()
    }
    operation("Чтение собаки", WfcDogProfileCommand.READ) {
        validateHasDogId()
    }
    operation("Обновление собаки", WfcDogProfileCommand.UPDATE) {
        trimDogName()
        validateHasDogId()
        validateHasUserId()
        validateDogName()
        validateDogHasWeight()
        validateDogHasAge()
        validateHasUserId()
        validateDescription()
    }
    operation("Список собак", WfcDogProfileCommand.LIST_ALL) {
        validateHasUserId()
    }
}

private fun WfcChain.validation(
    block: ICorChainDsl<WfcContext, WfcCorConfiguration>.() -> Unit
) = chain {
    title = "validation chain"
    on { state == WfcState.RUNNING }
    block()
}

private fun WfcChain.trimDogName() = worker {
    title = "Очистка имени собаки"
    doWork { dogProfileRequest = dogProfileRequest.copy(name = dogProfileRequest.name.trim()) }
}

private fun WfcChain.validateDogName() = worker {
    title = "Проверка наличия человекочитаемого имени и его длины"
    val regex = "^\\p{L}+$".toRegex()
    on {
        state == WfcState.RUNNING &&
                (with(dogProfileRequest) { !name.matches(regex) || name.length !in 3..50 })
    }
    doWork {
        fail(CODE_VALIDATION, "name", "noContent", "Name must consist of 3 to 50 letters")
    }
}

private fun WfcChain.validateDogHasWeight() = worker {
    title = "Проверка, что заполнен вес собаки"
    on {
        state == WfcState.RUNNING && dogProfileRequest.weight <= 0
    }
    doWork {
        fail(CODE_VALIDATION, "weight", "noContent", "Dog must weigh something")
    }
}

private fun WfcChain.validateDogHasAge() = worker {
    title = "Проверка, что заполнен вес собаки"
    on {
        state == WfcState.RUNNING && dogProfileRequest.age < 0
    }
    doWork {
        fail(CODE_VALIDATION, "age", "noContent", "Dog must have age")
    }
}

private fun WfcChain.validateHasUserId() = worker {
    title = "Проверка, что указан владелец"
    on {
        state == WfcState.RUNNING && dogProfileRequest.ownerId == WfcOwnerId.None
    }
    doWork {
        fail(CODE_VALIDATION, "ownerId", "noContent", "Dog must have an owner")
    }
}

private fun WfcChain.validateHasDogId() = worker {
    title = "Проверка, что указан id собаки"
    on {
        state == WfcState.RUNNING && dogProfileRequest.dogId == WfcDogId.None
    }
    doWork {
        fail(CODE_VALIDATION, "dogid", "noContent", "Dog ID must be present")
    }
}

private fun WfcChain.validateDescription() = worker {
    title = "Описание не пустое, если заполнено"
    on {
        state == WfcState.RUNNING && dogProfileRequest.description?.isBlank() == true
    }
    doWork {
        fail(CODE_VALIDATION, "description", "noContent", "Description is blank")
    }
}

