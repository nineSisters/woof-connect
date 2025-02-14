package ru.nnsh.woof_connect.stubs

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.nnsh.woof_connect.WfcCorConfiguration
import ru.nnsh.woof_connect.WfcProcessor
import ru.nnsh.woof_connect.common.WfcContext
import ru.nnsh.woof_connect.common.WfcError
import ru.nnsh.woof_connect.common.WfcState
import ru.nnsh.woof_connect.common.WfcWorkMode
import ru.nnsh.woof_connect.common.dog_profile.*
import ru.nnsh.woof_connect.common.stubs.stubDog
import ru.nnsh.woof_connect.logger.loggerFactory
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class StubTest {

    private val processor: WfcProcessor = WfcProcessor(WfcCorConfiguration(loggerFactory))
    val userId = WfcOwnerId(11)
    val dogId = WfcDogId(12)
    val dogName = "Sirok"

    @Test
    fun create() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.CREATE
            state = WfcState.NONE
            workMode = WfcWorkMode.STUB
            stub = WfcDogProfileStub.SUCCESS
            dogProfileRequest = WfcDogProfileBase(
                ownerId = userId,
                name = dogName,
            )
        }

        ctx.state = WfcState.RUNNING
        processor.invoke(ctx)
        assertEquals(userId, ctx.dogProfileResponse.ownerId)
        assertEquals(stubDog.dogId, ctx.dogProfileResponse.dogId)
        assertEquals(dogName, ctx.dogProfileResponse.name)
        assertEquals(WfcState.FINISHING, ctx.state)
    }

    @Test
    fun delete() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.DELETE
            state = WfcState.NONE
            workMode = WfcWorkMode.STUB
            stub = WfcDogProfileStub.SUCCESS
            dogProfileRequest = WfcDogProfileBase(
                ownerId = userId,
                dogId = dogId
            )
        }

        ctx.state = WfcState.RUNNING
        processor.invoke(ctx)
        assertEquals(userId, ctx.dogProfileResponse.ownerId)
        assertEquals(dogId, ctx.dogProfileResponse.dogId)
        assertEquals(WfcState.FINISHING, ctx.state)
    }

    @Test
    fun update() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.UPDATE
            state = WfcState.NONE
            workMode = WfcWorkMode.STUB
            stub = WfcDogProfileStub.SUCCESS
            dogProfileRequest = WfcDogProfileBase(
                ownerId = userId,
                dogId = dogId,
                name = dogName
            )
        }

        ctx.state = WfcState.RUNNING
        processor.invoke(ctx)
        assertEquals(userId, ctx.dogProfileResponse.ownerId)
        assertEquals(dogId, ctx.dogProfileResponse.dogId)
        assertEquals(dogName, ctx.dogProfileResponse.name)
        assertEquals(WfcState.FINISHING, ctx.state)
    }

    @Test
    fun read() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.READ
            state = WfcState.NONE
            workMode = WfcWorkMode.STUB
            stub = WfcDogProfileStub.SUCCESS
            dogProfileRequest = WfcDogProfileBase(
                ownerId = userId,
                dogId = dogId
            )
        }

        ctx.state = WfcState.RUNNING
        processor.invoke(ctx)
        assertEquals(userId, ctx.dogProfileResponse.ownerId)
        assertEquals(dogId, ctx.dogProfileResponse.dogId)
        assertEquals(stubDog.name, ctx.dogProfileResponse.name)
        assertEquals(stubDog.breed, ctx.dogProfileResponse.breed)
        assertEquals(stubDog.age, ctx.dogProfileResponse.age)
        assertEquals(stubDog.weight, ctx.dogProfileResponse.weight)
        assertEquals(WfcState.FINISHING, ctx.state)
    }

    @Test
    fun listAllDogsIds() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.LIST_ALL
            state = WfcState.NONE
            workMode = WfcWorkMode.STUB
            stub = WfcDogProfileStub.SUCCESS
        }

        ctx.state = WfcState.RUNNING
        processor.invoke(ctx)
        val expectedIds = List(10) { WfcDogId(it.toLong()) }
        assertContentEquals(expectedIds, ctx.allDogsResponse)
        assertEquals(WfcState.FINISHING, ctx.state)
    }

    @Test
    fun validationBadName() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.CREATE
            state = WfcState.NONE
            workMode = WfcWorkMode.STUB
            stub = WfcDogProfileStub.BAD_NAME
            dogProfileRequest = WfcDogProfileBase(
                ownerId = userId,
                name = dogName
            )
        }

        ctx.state = WfcState.RUNNING
        processor.invoke(ctx)
        assertEquals(WfcState.FAILING, ctx.state)
        assertEquals(WfcError.CODE_VALIDATION, ctx.error?.code)
        assertEquals("name", ctx.error?.field)
        assertEquals("Wrong name", ctx.error?.message)
    }

    @Test
    fun validationBadAge() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.CREATE
            state = WfcState.NONE
            workMode = WfcWorkMode.STUB
            stub = WfcDogProfileStub.BAD_AGE
            dogProfileRequest = WfcDogProfileBase(
                ownerId = userId,
                name = dogName
            )
        }

        ctx.state = WfcState.RUNNING
        processor.invoke(ctx)
        assertEquals(WfcState.FAILING, ctx.state)
        assertEquals(WfcError.CODE_VALIDATION, ctx.error?.code)
        assertEquals("name", ctx.error?.field)
        assertEquals("Wrong age", ctx.error?.message)
    }

    @Test
    fun bdErrorNoSuchUser() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.CREATE
            state = WfcState.NONE
            workMode = WfcWorkMode.STUB
            stub = WfcDogProfileStub.NOT_FOUND
            dogProfileRequest = WfcDogProfileBase(
                ownerId = userId,
                name = dogName
            )
        }

        ctx.state = WfcState.RUNNING
        processor.invoke(ctx)
        assertEquals(WfcState.FAILING, ctx.state)
        assertEquals(WfcError.CODE_DB, ctx.error?.code)
        assertEquals("name", ctx.error?.field)
        assertEquals("No such user", ctx.error?.message)
    }

    @Test
    fun noSuchStubsCase() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.CREATE
            state = WfcState.NONE
            workMode = WfcWorkMode.STUB
            stub = WfcDogProfileStub.NONE
        }

        ctx.state = WfcState.RUNNING
        processor.invoke(ctx)
        assertEquals(WfcState.FAILING, ctx.state)
        assertEquals(WfcError.CODE_SYSTEM, ctx.error?.code)
        assertEquals("stub", ctx.error?.field)
        assertEquals("No such stub", ctx.error?.message)
    }

    @Test
    fun validationOtherUserDog() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.DELETE
            state = WfcState.NONE
            workMode = WfcWorkMode.STUB
            stub = WfcDogProfileStub.BAD_OWNER_ID
            dogProfileRequest = WfcDogProfileBase(
                ownerId = userId,
                dogId = dogId
            )
        }

        ctx.state = WfcState.RUNNING
        processor.invoke(ctx)
        assertEquals(WfcState.FAILING, ctx.state)
        assertEquals(WfcError.CODE_VALIDATION, ctx.error?.code)
        assertEquals("ownerId", ctx.error?.field)
        assertEquals("Wrong user", ctx.error?.message)
    }

    @Test
    fun bdErrorNoSuchDog() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.READ
            state = WfcState.NONE
            workMode = WfcWorkMode.STUB
            stub = WfcDogProfileStub.NOT_FOUND
            dogProfileRequest = WfcDogProfileBase(
                ownerId = userId,
                dogId = dogId
            )
        }

        ctx.state = WfcState.RUNNING
        processor.invoke(ctx)
        assertEquals(WfcState.FAILING, ctx.state)
        assertEquals(WfcError.CODE_DB, ctx.error?.code)
        assertEquals("dogId", ctx.error?.field)
        assertEquals("Dog not found", ctx.error?.message)
    }
}