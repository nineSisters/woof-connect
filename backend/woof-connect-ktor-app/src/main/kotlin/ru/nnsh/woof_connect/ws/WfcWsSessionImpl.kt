package ru.nnsh.woof_connect.ws

import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.close
import ru.nnsh.woof_connect.api.v1.models.BaseRequest
import ru.nnsh.woof_connect.api.v1.models.BaseResponse
import ru.nnsh.woof_connect.common.ws.WfcWsSession

internal data class WfcWsSessionImpl(
    internal val session: DefaultWebSocketServerSession
) : WfcWsSession, DefaultWebSocketSession by session {
    val id = session.call
    override suspend fun close() = session.close()
    internal suspend fun <T> send(payload: T, type: TypeInfo) = session.sendSerialized(payload, type)
    internal suspend fun <T> receive(type: TypeInfo): T = session.receiveDeserialized(type)
}

internal suspend inline fun <reified T: BaseResponse> WfcWsSession.send(any: T) = (this as WfcWsSessionImpl).send(any, typeInfo<T>())
internal suspend inline fun <reified T: BaseRequest> WfcWsSession.receive(): T = (this as WfcWsSessionImpl).receive(typeInfo<T>())