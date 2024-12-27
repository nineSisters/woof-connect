import org.junit.jupiter.api.Test
import ru.nnsh.woof_connect.api.v1.models.*
import ru.nnsh.woof_connect.common.models.WfcContext
import ru.nnsh.woof_connect.common.models.WfcState
import ru.nnsh.woof_connect.common.models.WfcWorkMode
import ru.nnsh.woof_connect.common.models.dog_profile.*
import ru.nnsh.woof_connect.mappers.fromTransport
import ru.nnsh.woof_connect.mappers.toTransportResponse
import kotlin.test.*

class MappersTest {
    @Test
    fun fromTransport() {
        val request = DogProfileCreateRequest(
            dogProfile = DogProfileBase(
                ownerId = UserId(1),
                dogId = DogId(2),
                name = "Chappy",
                breed = "Mongrel",
                age = 3,
                weight = 21.5f,
                description = "Happy merry dog",
                photoUrl = null
            ),
            debug = DogProfileDebug(
                mode = DogProfileRequestDebugMode.STUB,
                DogProfileRequestDebugStubs.SUCCESS
            )
        )
        val context = WfcContext().fromTransport(request)

        assertEquals(WfcDogProfileStub.SUCCESS, context.stub)
        assertEquals(WfcWorkMode.STUB, context.workMode)
        assertEquals(WfcDogProfileCommand.CREATE, context.command)
        assertEquals(WfcDogId(2), context.dogProfileRequest.dogId)
        assertEquals(WfcOwnerId(1), context.dogProfileRequest.ownerId)
        assertEquals("Chappy", context.dogProfileRequest.name)
        assertEquals("Mongrel", context.dogProfileRequest.breed)
    }

    @Test
    fun toTransport() {
        val context = WfcContext().apply {
            command = WfcDogProfileCommand.CREATE
            stub = WfcDogProfileStub.SUCCESS
            workMode = WfcWorkMode.STUB
            state = WfcState.RUNNING
            dogProfileResponse = WfcDogProfileBase(
                ownerId = WfcOwnerId(1),
                dogId = WfcDogId(2),
                name = "Chappy",
                breed = "Mongrel",
                age = 3,
                weight = 21.5f,
                description = "Happy merry dog",
                photoUrl = null
            )
        }

        val response = context.toTransportResponse() as DogProfileCreateResponse

        assertFalse(response.isSuccess)
        assertNull(response.error)
        assertNotNull(response.dogId)
        assertEquals(DogId(2), response.dogId)
    }
}