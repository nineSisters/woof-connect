package ru.nnsh.woof_connect

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.jackson.JacksonConverter
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.serverConfig
import io.ktor.server.cio.CIO
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import ru.nnsh.woof_connect.controller.routeDogProfile
import ru.nnsh.woof_connect.serializer.apiV1ObjectMapper
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

internal fun Application.module() {
    routingModule()
    serializationModule()
    monitoringModule()
}

private fun Application.routingModule() {
    routing {
        get("/") {
            call.respondText("Hi")
        }
        routeDogProfile()
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

