package ru.nnsh.woof_connect.common.ws

interface WfcWsSession {
    suspend fun close()
}