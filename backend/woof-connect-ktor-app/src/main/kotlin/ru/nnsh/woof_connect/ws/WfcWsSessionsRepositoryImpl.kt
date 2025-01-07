package ru.nnsh.woof_connect.ws

import io.ktor.util.collections.ConcurrentSet
import ru.nnsh.woof_connect.api.v1.models.BaseResponse
import ru.nnsh.woof_connect.common.ws.WfcWsSession
import ru.nnsh.woof_connect.common.ws.WfcWsSessionRepository

class WfcWsSessionsRepositoryImpl: WfcWsSessionRepository {

    private val sessionsSet = ConcurrentSet<WfcWsSession>()

    override fun add(session: WfcWsSession): Boolean = sessionsSet.add(session)

    override suspend fun clear() {
        sessionsSet.forEach {
            sessionsSet.remove(it)
//            (it as WfcWsSessionImpl).close(CloseReason(CloseReason.Codes.GOING_AWAY, "Server is going away"))
        }
    }

    override fun remove(session: WfcWsSession) {
        sessionsSet.remove(session)
    }

    override suspend fun <P> broadcast(payload: P) {
        require(payload is BaseResponse)
        sessionsSet.forEach {
            it.send(payload)
        }
    }
}