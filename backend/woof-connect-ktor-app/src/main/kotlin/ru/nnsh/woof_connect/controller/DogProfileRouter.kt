package ru.nnsh.woof_connect.controller

import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.utils.io.KtorDsl
import ru.nnsh.woof_connect.api.v1.models.DogProfileCreateRequest
import ru.nnsh.woof_connect.api.v1.models.DogProfileDeleteRequest
import ru.nnsh.woof_connect.api.v1.models.DogProfileReadRequest
import ru.nnsh.woof_connect.api.v1.models.DogProfileUpdateRequest
import ru.nnsh.woof_connect.api.v1.models.UserDogIdsRequest
import ru.nnsh.woof_connect.common.WfcContext
import ru.nnsh.woof_connect.common.stubs.processAsStub

@KtorDsl
internal fun Routing.routeDogProfile(
    processor: suspend WfcContext.() -> Unit = { processAsStub() }
) {
    post("/dog-profile/create") {
        processApiV1<DogProfileCreateRequest>(processor)
    }
    post("/dog-profile/delete") {
        processApiV1<DogProfileDeleteRequest>(processor)
    }
    post("/dog-profile/update") {
        processApiV1<DogProfileUpdateRequest>(processor)
    }
    post("/dog-profile/read") {
        processApiV1<DogProfileReadRequest>(processor)
    }
    post("/dog-profile/read") {
        processApiV1<DogProfileReadRequest>(processor)
    }
    post("/dog-profile/list") {
        processApiV1<UserDogIdsRequest>(processor)
    }
}