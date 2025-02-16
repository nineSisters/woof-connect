package ru.nnsh.woof_connect.repository

import ru.nanashi.ru.nnsh.cor.impl.worker
import ru.nnsh.woof_connect.WfcChain
import ru.nnsh.woof_connect.common.WfcState
import ru.nnsh.woof_connect.onRunning

fun WfcChain.prepareResult() = worker {
    title = "Подготовка данных для ответа клиенту"
    onRunning()
    doWork {
        state = WfcState.FINISHING
        dogProfileResponse = dogProfileDone
    }
}
