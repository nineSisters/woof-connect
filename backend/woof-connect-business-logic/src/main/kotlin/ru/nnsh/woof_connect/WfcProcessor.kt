package ru.nnsh.woof_connect

import ru.nanashi.ru.nnsh.cor.dsl.ICorChainDsl
import ru.nanashi.ru.nnsh.cor.dsl.ICorWorkerDsl
import ru.nanashi.ru.nnsh.cor.impl.chain
import ru.nanashi.ru.nnsh.cor.impl.rootChain
import ru.nnsh.woof_connect.common.WfcContext
import ru.nnsh.woof_connect.common.WfcState
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileCommand
import ru.nnsh.woof_connect.stubs.stubsChain

typealias WfcProcessor = suspend WfcContext.() -> Unit
typealias WfcChain = ICorChainDsl<WfcContext, WfcCorConfiguration>
typealias WfcWorker = ICorWorkerDsl<WfcContext, WfcCorConfiguration>

fun WfcProcessor(
    corConfiguration: WfcCorConfiguration
): WfcProcessor = rootChain<WfcContext, WfcCorConfiguration>(corConfiguration) {
    stubsChain()
}
    .build()::execute

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
