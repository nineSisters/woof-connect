package ru.nnsh.woof_connect.common

import org.slf4j.ILoggerFactory
import ru.nnsh.woof_connect.common.repository.IDogProfileRepository
import ru.nnsh.woof_connect.common.ws.WfcWsSessionRepository

class WfcCorConfiguration(
    val loggerFactory: ILoggerFactory,
    val wsSessionRepository: WfcWsSessionRepository,
//    val stubRepository: IDogProfileRepository = IDogProfileRepository.NONE,

    val testRepository: IDogProfileRepository = IDogProfileRepository.NONE,
    val prodRepository: IDogProfileRepository = IDogProfileRepository.NONE
)
