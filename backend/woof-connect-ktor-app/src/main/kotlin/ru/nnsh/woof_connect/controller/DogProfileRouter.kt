package ru.nnsh.woof_connect.controller

import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.utils.io.KtorDsl
import ru.nnsh.woof_connect.WfcProcessor

private const val REST_LOG_TAG = "REST controller"

@KtorDsl
internal fun Routing.routeDogProfile(
    processor: WfcProcessor
) {
    post("/dog-profile/create") {
        call.processApiV1(REST_LOG_TAG, processor, call::receive) { call.respond(it) }
    }
    post("/dog-profile/delete") {
        call.processApiV1(REST_LOG_TAG, processor, call::receive) { call.respond(it) }
    }
    post("/dog-profile/update") {
        call.processApiV1(REST_LOG_TAG, processor, call::receive) { call.respond(it) }
    }
    post("/dog-profile/read") {
        call.processApiV1(REST_LOG_TAG, processor, call::receive) { call.respond(it) }
    }
    post("/dog-profile/read") {
        call.processApiV1(REST_LOG_TAG, processor, call::receive) { call.respond(it) }
    }
    post("/dog-profile/list") {
        call.processApiV1(REST_LOG_TAG, processor, call::receive) { call.respond(it) }
    }
}