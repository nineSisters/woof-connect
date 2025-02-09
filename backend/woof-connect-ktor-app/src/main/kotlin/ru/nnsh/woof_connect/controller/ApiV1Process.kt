package ru.nnsh.woof_connect.controller

import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.datetime.Clock
import org.slf4j.LoggerFactory
import ru.nnsh.woof_connect.WfcProcessor
import ru.nnsh.woof_connect.api.v1.models.BaseRequest
import ru.nnsh.woof_connect.api.v1.models.BaseResponse
import ru.nnsh.woof_connect.common.WfcContext
import ru.nnsh.woof_connect.common.WfcError
import ru.nnsh.woof_connect.common.WfcRequestId
import ru.nnsh.woof_connect.common.WfcState
import ru.nnsh.woof_connect.logger.loggerFactory
import ru.nnsh.woof_connect.mappers.fromTransport
import ru.nnsh.woof_connect.mappers.toTransportResponse
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal suspend inline fun ApplicationCall.processApiV1(
    logTag: String,
    crossinline processor: WfcProcessor,
    crossinline getRequest: suspend () -> BaseRequest,
    crossinline sendResponse: suspend (BaseResponse) -> Unit,
) {
    val logger = loggerFactory.getLogger(logTag)
    WfcContext {
        timeStart = Clock.System.now()
        val uuid = callId!!
        requestId = WfcRequestId(Uuid.parse(uuid))

        try {
            logger.info("Request $uuid started")
            val request = getRequest()
            logger.info("Request $uuid of type ${request.requestType} received")
            fromTransport(request)
            state = WfcState.RUNNING
            processor()
            if (state == WfcState.RUNNING) {
                state = WfcState.FINISHING
            }
            logger.info("Request $uuid of type ${request.requestType} processed")
            sendResponse(toTransportResponse())
        } catch (e: ClosedReceiveChannelException) {
            logger.error("Channel is closed for request $uuid", e) // should not be the case
        } catch (e: Exception) {
            logger.error("Request $uuid failed", e)
            state = WfcState.FAILING
            error = WfcError(e)
            sendResponse(toTransportResponse())
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
internal suspend inline fun ApplicationCall.pushApiV1(
    logTag: String,
    crossinline initContext: WfcContext.() -> Unit,
    crossinline processor: suspend WfcContext.() -> Unit,
    crossinline sendResponse: suspend (BaseResponse) -> Unit,
) {
    val logger = LoggerFactory.getLogger(logTag)
    WfcContext {
        initContext()
        timeStart = Clock.System.now()
        val uuid = callId!!
        requestId = WfcRequestId(Uuid.parse(uuid))

        try {
            logger.info("Push $uuid of command $command started")
            processor()
            state = WfcState.FINISHING
            logger.info("Push $uuid of command $command processed")
            sendResponse(toTransportResponse())
        } catch (e: ClosedReceiveChannelException) {
            logger.error("Channel is closed for push $uuid", e)
        } catch (e: Exception) {
            logger.error("Push $uuid failed", e)
            state = WfcState.FAILING
            error = WfcError(e)
            sendResponse(toTransportResponse())
        }
    }
}
