package ru.nnsh.woof_connect.stubs

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.nnsh.woof_connect.WfcCorConfiguration
import ru.nnsh.woof_connect.WfcProcessor
import ru.nnsh.woof_connect.common.WfcContext
import ru.nnsh.woof_connect.common.WfcState
import ru.nnsh.woof_connect.common.WfcWorkMode
import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileCommand
import ru.nnsh.woof_connect.common.dog_profile.WfcOwnerId
import ru.nnsh.woof_connect.common.stubs.stubDog
import ru.nnsh.woof_connect.logger.loggerFactory
import kotlin.test.assertEquals

class ValidationTest {

    private val processor: WfcProcessor = WfcProcessor(WfcCorConfiguration(loggerFactory))
    val userId = WfcOwnerId(11)
    val dogId = WfcDogId(12)


    @Test
    fun trimDogName() = runTest {
        val ctx = WfcContext {
            workMode = WfcWorkMode.TEST
            command = WfcDogProfileCommand.CREATE
            state = WfcState.RUNNING
            dogProfileRequest = stubDog.copy(
                name = "  Fluffy  "
            )
        }

        processor.invoke(ctx)
        assertEquals("Fluffy", ctx.dogProfileRequest.name)
        assertEquals(WfcState.RUNNING, ctx.state)
    }

    @Test
    fun validateDogName() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.CREATE
            state = WfcState.NONE
            dogProfileRequest = WfcDogProfileBase(
                name = "Fl"
            )
        }

        ctx.state = WfcState.RUNNING
        processor.invoke(ctx)
        assertEquals(WfcState.FAILING, ctx.state)
        assertEquals("name", ctx.error?.field)
        assertEquals("noContent", ctx.error?.group)
    }

    @Test
    fun validateDogHasWeight() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.CREATE
            state = WfcState.RUNNING
            dogProfileRequest = stubDog.copy(weight = 0f)
        }

        processor.invoke(ctx)
        assertEquals(WfcState.FAILING, ctx.state)
        assertEquals("weight", ctx.error?.field)
        assertEquals("noContent", ctx.error?.group)
    }

    @Test
    fun validateDogHasAge() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.CREATE
            state = WfcState.RUNNING
            dogProfileRequest = stubDog.copy(
                age = -1
            )
        }

        processor.invoke(ctx)
        assertEquals(WfcState.FAILING, ctx.state)
        assertEquals("age", ctx.error?.field)
        assertEquals("noContent", ctx.error?.group)
    }

    @Test
    fun validateHasUserId() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.CREATE
            state = WfcState.RUNNING
            dogProfileRequest = stubDog.copy(
                ownerId = WfcOwnerId.None
            )
        }

        processor.invoke(ctx)
        assertEquals(WfcState.FAILING, ctx.state)
        assertEquals("ownerId", ctx.error?.field)
        assertEquals("noContent", ctx.error?.group)
    }

    @Test
    fun validateHasDogId() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.DELETE
            state = WfcState.RUNNING
            dogProfileRequest = WfcDogProfileBase(
                ownerId = WfcOwnerId(11),
                dogId = WfcDogId.None
            )
        }

        processor.invoke(ctx)
        assertEquals(WfcState.FAILING, ctx.state)
        assertEquals("dogid", ctx.error?.field)
        assertEquals("noContent", ctx.error?.group)
    }

    @Test
    fun validateDescription() = runTest {
        val ctx = WfcContext {
            command = WfcDogProfileCommand.CREATE
            state = WfcState.RUNNING
            dogProfileRequest = stubDog.copy(
                description = "   "
            )
        }

        processor.invoke(ctx)
        assertEquals(WfcState.FAILING, ctx.state)
        assertEquals("description", ctx.error?.field)
        assertEquals("noContent", ctx.error?.group)
    }
}