package ru.nnsh.woof_connect

import ru.nanashi.ru.nnsh.cor.dsl.CorDsl
import ru.nanashi.ru.nnsh.cor.dsl.ICorChainDsl
import ru.nanashi.ru.nnsh.cor.dsl.ICorWorkerDsl
import ru.nanashi.ru.nnsh.cor.impl.chain
import ru.nanashi.ru.nnsh.cor.impl.rootChain
import ru.nnsh.woof_connect.common.WfcContext
import ru.nnsh.woof_connect.common.WfcCorConfiguration
import ru.nnsh.woof_connect.common.WfcState
import ru.nnsh.woof_connect.common.WfcWorkMode
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileCommand
import ru.nnsh.woof_connect.repository.*
import ru.nnsh.woof_connect.stubs.*
import ru.nnsh.woof_connect.validation.*

typealias WfcProcessor = suspend WfcContext.() -> Unit
typealias WfcChain = ICorChainDsl<WfcContext, WfcCorConfiguration>
typealias WfcWorker = ICorWorkerDsl<WfcContext, WfcCorConfiguration>

fun WfcProcessor(
    corConfiguration: WfcCorConfiguration
): WfcProcessor = rootChain<WfcContext, WfcCorConfiguration>(corConfiguration) {
    initRepository()
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
        chain {
            title = "Логика сохранения"
            prepareCreateDog()
            createDog()
            prepareResult()
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
        chain {
            title = "Логика удаления"
            readDog()
            prepareDeleteDog()
            deleteDog()
            prepareResult()
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
        chain {
            title = "Логика чтения"
            readDog()
            prepareReadDog()
            prepareResult()
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
        }
        chain {
            title = "Логика удаления"
            readDog()
            prepareUpdateDog()
            updateDog()
            prepareResult()
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
        chain {
            title = "Выборка собак"
            prepareListDogs()
            listDogs()
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

@CorDsl
internal fun WfcWorker.onRunning() {
    on { state == WfcState.RUNNING }
}
