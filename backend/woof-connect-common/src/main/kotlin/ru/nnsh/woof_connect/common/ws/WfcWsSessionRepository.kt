package ru.nnsh.woof_connect.common.ws

interface WfcWsSessionRepository {

    fun add(session: WfcWsSession): Boolean
    suspend fun clear()
    fun remove(session: WfcWsSession)
    suspend fun <P> broadcast(payload: P)

    companion object {
        val NONE = object : WfcWsSessionRepository {
            override fun add(session: WfcWsSession) = true
            override suspend fun clear() = Unit
            override fun remove(session: WfcWsSession) = Unit
            override suspend fun <P> broadcast(payload: P) = Unit

        }
    }

}