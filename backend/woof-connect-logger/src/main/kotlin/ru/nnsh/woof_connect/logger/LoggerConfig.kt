package ru.nnsh.woof_connect.logger

import org.slf4j.ILoggerFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.spi.LoggingEventBuilder

val loggerFactory: ILoggerFactory = LoggerFactory.getILoggerFactory()

fun LoggingEventBuilder.addTraceId(id: String): LoggingEventBuilder = addKeyValue("traceId", id)

inline fun Logger.doWithLogging(id: String, block: () -> Unit) {
    atInfo()
        .addTraceId(id)
        .log("Started work")
    try {
        block()
        atInfo()
            .addTraceId(id)
            .log("Finished work")
    } catch (exception: Exception) {
        atError()
            .addTraceId(id)
            .setCause(exception)
            .log("Failed work")
        throw exception
    }
}
