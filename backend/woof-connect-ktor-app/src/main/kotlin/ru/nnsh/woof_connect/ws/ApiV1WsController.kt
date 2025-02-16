package ru.nnsh.woof_connect.ws

import io.ktor.server.websocket.*
import org.slf4j.LoggerFactory
import ru.nnsh.woof_connect.WfcProcessor
import ru.nnsh.woof_connect.api.v1.models.BaseRequest
import ru.nnsh.woof_connect.common.WfcCorConfiguration
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileCommand
import ru.nnsh.woof_connect.common.ws.WfcWsSession
import ru.nnsh.woof_connect.controller.processApiV1
import ru.nnsh.woof_connect.controller.pushApiV1

private const val WS_LOG_TAG = "WS controller"

suspend fun DefaultWebSocketServerSession.processWsSession(
    wfcCorConfiguration: WfcCorConfiguration,
    processor: WfcProcessor = WfcProcessor(wfcCorConfiguration)
) {

    val session = WfcWsSessionImpl(this)
    wfcCorConfiguration.wsSessionRepository.add(session)
    call.pushApiV1(
        WS_LOG_TAG,
        initContext = {
            command = WfcDogProfileCommand.INIT_WS
        },
        {},
        session::send
    )

    while (true) {
        val request = session.receiveWsRequestInternal() ?: break
        call.processApiV1(
            logTag = WS_LOG_TAG,
            processor = {
                wsSession = session
                processor()
            },
            getRequest = { request },
            sendResponse = session::send
        )
    }
    LoggerFactory.getLogger(WS_LOG_TAG)
    wfcCorConfiguration.wsSessionRepository.remove(session)
}

private suspend fun WfcWsSession.receiveWsRequestInternal(): BaseRequest? = runCatching {
    receive<BaseRequest>()
}.getOrNull()
