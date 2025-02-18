package ru.nnsh.woof_connect

import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import ru.nnsh.woof_connect.common.WfcCorConfiguration
import ru.nnsh.woof_connect.common.ws.WfcWsSessionRepository
import ru.nnsh.woof_connect.controller.routeDogProfile
import ru.nnsh.woof_connect.logger.loggerFactory
import ru.nnsh.woof_connect.serializer.apiV1ObjectMapper
import ru.nnsh.woof_connect.ws.WfcWsSessionsRepositoryImpl
import ru.nnsh.woof_connect.ws.processWsSession
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import ru.nnsh.woof_connect.repository.DogProfileInMemoryRepository
import ru.nnsh.woof_connect.repository.DogProfilePgRepository

fun main() {
    embeddedServer(
        CIO,
        serverConfig {
            this.module { module() }
        }
    ) {
        connector {
            port = 8080
            host = "0.0.0.0"
        }
    }.start(wait = true)
}

internal fun Application.module(
    wfcCorConfiguration: WfcCorConfiguration = WfcCorConfiguration(
        loggerFactory,
        WfcWsSessionsRepositoryImpl(),
        testRepository = DogProfileInMemoryRepository(),
        prodRepository = DogProfilePgRepository()
    )
) {
    serializationModule()
    monitoringModule()
    webSocketModule(wfcCorConfiguration)
    routingModule(wfcCorConfiguration)
}

private fun Application.routingModule(wfcCorConfiguration: WfcCorConfiguration) {
    routing {
        get("/") {
            call.respondText("Hi")
        }
        routeDogProfile(
            WfcProcessor(wfcCorConfiguration)
        )
    }
}

private fun Application.serializationModule() {
    install(ContentNegotiation) {
        val converter = JacksonConverter(apiV1ObjectMapper, streamRequestBody = false)
        register(ContentType.Application.Json, converter)
    }
}

@OptIn(ExperimentalUuidApi::class)
private fun Application.monitoringModule() {
    install(CallId) {
        header(HttpHeaders.XRequestId)
        verify { id -> kotlin.runCatching { Uuid.parse(id) }.isSuccess }
        generate {
            Uuid.random().toString()
        }
    }
}

private fun Application.webSocketModule(
    wfcCorConfiguration: WfcCorConfiguration
) {
    install(WebSockets) {
        contentConverter = JacksonWebsocketContentConverter(apiV1ObjectMapper)
        pingPeriodMillis = 15_000
        timeoutMillis = 300_000
    }
    routing {
        webSocket("/ws") {
            processWsSession(wfcCorConfiguration)
        }
    }
}
