package ru.nnsh.woof_connect.stubs

import ru.nanashi.ru.nnsh.cor.dsl.ICorChainDsl
import ru.nanashi.ru.nnsh.cor.impl.chain
import ru.nnsh.woof_connect.WfcChain
import ru.nnsh.woof_connect.WfcCorConfiguration
import ru.nnsh.woof_connect.common.WfcContext
import ru.nnsh.woof_connect.common.WfcState
import ru.nnsh.woof_connect.common.WfcWorkMode
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileCommand
import ru.nnsh.woof_connect.operation

internal fun WfcChain.stubsChain() = stubs {
    operation("Регистрация собаки", WfcDogProfileCommand.CREATE) {
        stubCreateSuccess("Имитация успешной обработки")
        stubValidationBadAge("Имитация ошибки валидации возраста")
        stubValidationBadName("Имитация ошибки валидации имени")
        stubBdErrorNoSuchUser("Имитация ошибки базы данных")
        stubNoSuchStubsCase("Недопустимый стаб")
    }
    operation("Удаление собаки", WfcDogProfileCommand.DELETE) {
        stubDeleteSuccess("Имитация успешной обработки")
        stubBdErrorNoSuchDog("Имитация ошибки базы данных")
        stubValidationOtherUserDog("Имитация попытки удалить чужую собаку")
        stubNoSuchStubsCase("Недопустимый стаб")
    }
    operation("Чтение собаки", WfcDogProfileCommand.READ) {
        stubReadSuccess("Имитация успешной обработки")
        stubBdErrorNoSuchDog("Имитация ошибки базы данных")
        stubNoSuchStubsCase("Недопустимый стаб")
    }
    operation("Обновление собаки", WfcDogProfileCommand.UPDATE) {
        stubUpdateSuccess("Имитация успешной обработки")
        stubValidationBadAge("Имитация ошибки валидации возраста")
        stubValidationBadName("Имитация ошибки валидации имени")
        stubBdErrorNoSuchDog("Имитация ошибки базы данных")
        stubNoSuchStubsCase("Недопустимый стаб")
    }
    operation("Список собак", WfcDogProfileCommand.LIST_ALL) {
        stubListAllDogsIdsSuccess("Имитация успешной обработки")
        stubBdErrorNoSuchUser("Имитация ошибки базы данных")
        stubNoSuchStubsCase("Недопустимый стаб")
    }
}

private fun WfcChain.stubs(
    block: ICorChainDsl<WfcContext, WfcCorConfiguration>.() -> Unit
) = chain {
    title = "stubs chain"
    on { workMode == WfcWorkMode.STUB && state == WfcState.RUNNING }
    block()
}