package ru.nnsh.woof_connect

import ru.nanashi.ru.nnsh.cor.dsl.ICorChainDsl
import ru.nanashi.ru.nnsh.cor.dsl.ICorWorkerDsl
import ru.nanashi.ru.nnsh.cor.impl.chain
import ru.nanashi.ru.nnsh.cor.impl.rootChain
import ru.nnsh.woof_connect.common.WfcContext
import ru.nnsh.woof_connect.common.WfcState
import ru.nnsh.woof_connect.common.WfcWorkMode
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileCommand
import ru.nnsh.woof_connect.stubs.*
import ru.nnsh.woof_connect.validation.*

typealias WfcProcessor = suspend WfcContext.() -> Unit
typealias WfcChain = ICorChainDsl<WfcContext, WfcCorConfiguration>
typealias WfcWorker = ICorWorkerDsl<WfcContext, WfcCorConfiguration>

fun WfcProcessor(
    corConfiguration: WfcCorConfiguration
): WfcProcessor = rootChain<WfcContext, WfcCorConfiguration>(corConfiguration) {
    operation("Регистрация собаки", WfcDogProfileCommand.CREATE) {
        stubs {
            stubCreateSuccess("Имитация успешной обработки")
            stubValidationBadAge("Имитация ошибки валидации возраста")
            stubValidationBadName("Имитация ошибки валидации имени")
            stubBdErrorNoSuchUser("Имитация ошибки базы данных")
            stubNoSuchStubsCase("Недопустимый стаб")
        }
        validation {
            trimDogName()
            validateDogName()
            validateDogHasWeight()
            validateDogHasAge()
            validateHasUserId()
            validateDescription()
        }
    }
    operation("Удаление собаки", WfcDogProfileCommand.DELETE) {
        stubs {
            stubDeleteSuccess("Имитация успешной обработки")
            stubBdErrorNoSuchDog("Имитация ошибки базы данных")
            stubValidationOtherUserDog("Имитация попытки удалить чужую собаку")
            stubNoSuchStubsCase("Недопустимый стаб")
        }
        validation {
            validateHasUserId()
            validateHasDogId()
        }
    }
    operation("Чтение собаки", WfcDogProfileCommand.READ) {
        stubs {
            stubReadSuccess("Имитация успешной обработки")
            stubBdErrorNoSuchDog("Имитация ошибки базы данных")
            stubNoSuchStubsCase("Недопустимый стаб")
        }
        validation {
            validateHasDogId()
        }
    }
    operation("Обновление собаки", WfcDogProfileCommand.UPDATE) {
        stubs {
            stubUpdateSuccess("Имитация успешной обработки")
            stubValidationBadAge("Имитация ошибки валидации возраста")
            stubValidationBadName("Имитация ошибки валидации имени")
            stubBdErrorNoSuchDog("Имитация ошибки базы данных")
            stubNoSuchStubsCase("Недопустимый стаб")
        }
        validation {
            trimDogName()
            validateHasDogId()
            validateHasUserId()
            validateDogName()
            validateDogHasWeight()
            validateDogHasAge()
            validateHasUserId()
            validateDescription()
        }
    }
    operation("Список собак", WfcDogProfileCommand.LIST_ALL) {
        stubs {
            stubListAllDogsIdsSuccess("Имитация успешной обработки")
            stubBdErrorNoSuchUser("Имитация ошибки базы данных")
            stubNoSuchStubsCase("Недопустимый стаб")
        }
        validation {
            validateHasUserId()
        }
    }
}.build()::execute

internal fun WfcChain.operation(
    title: String,
    command: WfcDogProfileCommand,
    block: ICorChainDsl<WfcContext, WfcCorConfiguration>.() -> Unit
) = chain {
    this.title = title
    on {
        this.command == command && state == WfcState.RUNNING
    }
    block()
}

internal fun WfcChain.stubs(
    block: ICorChainDsl<WfcContext, WfcCorConfiguration>.() -> Unit
) = chain {
    title = "stubs chain"
    on { workMode == WfcWorkMode.STUB && state == WfcState.RUNNING }
    block()
}
