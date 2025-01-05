package ru.nnsh.woof_connect.controller

import io.ktor.callid.KtorCallIdContextElement
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext
import kotlinx.datetime.Clock
import org.slf4j.LoggerFactory
import ru.nnsh.woof_connect.api.v1.models.BaseRequest
import ru.nnsh.woof_connect.common.models.WfcContext
import ru.nnsh.woof_connect.common.models.WfcError
import ru.nnsh.woof_connect.common.models.WfcRequestId
import ru.nnsh.woof_connect.common.models.WfcState
import ru.nnsh.woof_connect.mappers.fromTransport
import ru.nnsh.woof_connect.mappers.toTransportResponse
import kotlin.coroutines.coroutineContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal suspend inline fun <reified T : BaseRequest> RoutingContext.processApiV1(
    crossinline processor: suspend WfcContext.() -> Unit
) {
    val logger = LoggerFactory.getLogger(T::class.simpleName)
    val ctx = WfcContext()
    ctx {
        timeStart = Clock.System.now()
        val uuid = coroutineContext[KtorCallIdContextElement]?.callId?.let(Uuid::parse) ?: Uuid.random()
        requestId = WfcRequestId(uuid)

        try {
            logger.info("Request $requestId started")
            fromTransport(call.receive())
            processor()
            state = WfcState.FINISHING
            logger.info("Request $requestId processed")
        } catch (e: Exception) {
            logger.error("Request $requestId failed")
            state = WfcState.FAILING
            error = WfcError(e)
        } finally {
            call.respond(toTransportResponse())
        }
    }
}