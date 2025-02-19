package ru.nnsh.woof_connect.stubs

import kotlinx.coroutines.test.runTest
import ru.nnsh.woof_connect.WfcProcessor
import ru.nnsh.woof_connect.common.*
import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase.Companion.mergeWith
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileCommand
import ru.nnsh.woof_connect.common.dog_profile.WfcOwnerId
import ru.nnsh.woof_connect.common.repository.DbDogIdRequest
import ru.nnsh.woof_connect.common.repository.DbDogProfileRequest
import ru.nnsh.woof_connect.common.repository.DbOwnerIdRequest
import ru.nnsh.woof_connect.common.repository.IDbResponse
import ru.nnsh.woof_connect.common.stubs.stubDog
import ru.nnsh.woof_connect.logger.loggerFactory
import ru.nnsh.woof_connect.repository.DogProfileRepositoryMock
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class BizRepositoryDogTest {

    private val repository = object : DogProfileRepositoryMock() {
        override suspend fun readDog(request: DbDogIdRequest): IDbResponse<WfcDogProfileBase> {
            return if (request.dogId == stubDog.dogId) IDbResponse.DogProfile(stubDog) else IDbResponse.Err(WfcError.DOG_NOT_FOUND)
        }

        override suspend fun createDog(request: DbDogProfileRequest): IDbResponse<WfcDogProfileBase> {
            return IDbResponse.DogProfile(request.dog.copy(dogId = stubDog.dogId))
        }

        override suspend fun deleteDog(request: DbDogIdRequest): IDbResponse<WfcDogProfileBase> {
            return if (request.dogId == stubDog.dogId) IDbResponse.DogProfile(stubDog) else IDbResponse.Err(WfcError.DOG_NOT_FOUND)
        }

        override suspend fun updateDog(request: DbDogProfileRequest): IDbResponse<WfcDogProfileBase> {
            return IDbResponse.DogProfile(request.dog.mergeWith(stubDog))
        }

        override suspend fun listDogs(request: DbOwnerIdRequest): IDbResponse<List<WfcDogId>> {
            return IDbResponse.DogIds(List(10, ::WfcDogId))
        }
    }

    private val processor = WfcProcessor(WfcCorConfiguration(loggerFactory, testRepository = repository))

    @Test
    fun createDogSuccessTest() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.CREATE
            state = WfcState.RUNNING
            workMode = WfcWorkMode.TEST
            dogProfileRequest = stubDog.copy(dogId = WfcDogId.None)
        }
        processor(ctx)

        assertEquals(WfcState.FINISHING, ctx.state)
        assertEquals(stubDog.copy(ownerId = WfcOwnerId.TheOne), ctx.dogProfileResponse)
    }

    @Test
    fun readDogSuccessTest() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.READ
            state = WfcState.RUNNING
            workMode = WfcWorkMode.TEST
            dogProfileRequest = WfcDogProfileBase(dogId = stubDog.dogId)
        }
        processor(ctx)

        assertEquals(WfcState.FINISHING, ctx.state)
        assertEquals(stubDog, ctx.dogProfileResponse)
    }

    @Test
    fun readDogNotFoundTest() = notFoundTest(WfcDogProfileCommand.READ)

    @Test
    fun deleteDogSuccessTest() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.DELETE
            state = WfcState.RUNNING
            workMode = WfcWorkMode.TEST
            dogProfileRequest = WfcDogProfileBase(dogId = stubDog.dogId, ownerId = WfcOwnerId.TheOne)
        }
        processor(ctx)

        assertEquals(WfcState.FINISHING, ctx.state)
        assertEquals(stubDog, ctx.dogProfileResponse)

    }

    @Test
    fun deleteDogNotFoundTest() = notFoundTest(WfcDogProfileCommand.DELETE)

    @Test
    fun updateDogSuccessTest() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.UPDATE
            state = WfcState.RUNNING
            workMode = WfcWorkMode.TEST
            dogProfileRequest = WfcDogProfileBase(
                dogId = stubDog.dogId,
                name = "Tuzik",
                breed = "Husky",
                ownerId = WfcOwnerId(2)
            )
        }
        processor(ctx)
        val expectedDog = stubDog.copy(
            dogId = stubDog.dogId,
            name = "Tuzik",
            breed = "Husky",
            ownerId = WfcOwnerId(2),
        )
        assertEquals(WfcState.FINISHING, ctx.state)
        assertEquals(expectedDog, ctx.dogProfileResponse)
    }

    @Test
    fun listAllDogsSuccessTest() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.LIST_ALL
            state = WfcState.RUNNING
            workMode = WfcWorkMode.TEST
            dogProfileRequest = WfcDogProfileBase(ownerId = WfcOwnerId.TheOne)
        }
        processor(ctx)

        assertEquals(WfcState.FINISHING, ctx.state)
        assertContentEquals(List(10, ::WfcDogId), ctx.allDogsResponse)
    }

    private fun notFoundTest(command: WfcDogProfileCommand) = runTest {
        val ctx = WfcContext {
            this.command = command
            state = WfcState.RUNNING
            workMode = WfcWorkMode.TEST
            dogProfileRequest = WfcDogProfileBase(dogId = WfcDogId(-1), ownerId = WfcOwnerId.TheOne)
        }
        processor(ctx)

        assertEquals(WfcState.FAILING, ctx.state)
        assertEquals(WfcError.DOG_NOT_FOUND, ctx.error)
    }
}
