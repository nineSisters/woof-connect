package ru.nnsh.woof_connect.fixtures

import ru.nnsh.woof_connect.api.v1.models.*

class DogProfileTestFixtures(
    debugMode: DogProfileRequestDebugMode
) {

    private val successStubDebug = DogProfileDebug(debugMode, DogProfileRequestDebugStubs.SUCCESS)
    val notFoundStubDebug = DogProfileDebug(debugMode, DogProfileRequestDebugStubs.NOT_FOUND)

    val readRequest = DogProfileReadRequest(
        dogId = DogId(1),
        debug = successStubDebug
    )

    val createRequest = DogProfileCreateRequest(
        dogProfile = DogProfileBase(
            ownerId = UserId(1),
            dogId = DogId(0),
            name = "Bobby",
            age = 3,
            breed = "Golden Retriever",
        ),
        debug = successStubDebug
    )

    val updateRequest = DogProfileUpdateRequest(
        dogProfile = DogProfileBase(
            dogId = DogId(1),
            ownerId = UserId(11),
            name = "Rex",
            age = 4
        ),
        debug = successStubDebug
    )

    val deleteRequest = DogProfileDeleteRequest(
        dogId = DogId(1),
        ownerId = UserId(1),
        debug = successStubDebug
    )

    val listDogsRequest = UserDogIdsRequest(
        userId = UserId(1),
        debug = successStubDebug
    )
}
