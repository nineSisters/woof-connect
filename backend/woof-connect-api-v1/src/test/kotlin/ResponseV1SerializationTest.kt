import org.junit.jupiter.api.Test
import ru.nnsh.woof_connect.common.WfcContext
import ru.nnsh.woof_connect.common.WfcState
import ru.nnsh.woof_connect.common.WfcWorkMode
import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileCommand
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileStub
import ru.nnsh.woof_connect.common.dog_profile.WfcOwnerId
import ru.nnsh.woof_connect.mappers.toTransportResponse
import ru.nnsh.woof_connect.serializer.apiV1ResponseSerialize
import kotlin.test.assertContains

class ResponseV1SerializationTest {
    private val response = WfcContext().apply {
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
    }.toTransportResponse()

    @Test
    fun serialize() {
        val json = response.apiV1ResponseSerialize()

        assertContains(json, Regex("\"responseType\":\\s*\"create\""))
        assertContains(json, Regex("\"dogId\":\\s*2"))
    }

}
