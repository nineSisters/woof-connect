import org.junit.jupiter.api.Test
import ru.nnsh.woof_connect.api.v1.models.*
import ru.nnsh.woof_connect.serializer.apiV1RequestDeserialize
import ru.nnsh.woof_connect.serializer.apiV1RequestSerialize
import kotlin.test.assertContains
import kotlin.test.assertEquals

class RequestV1SerializationTest {

     private val request = DogProfileCreateRequest(
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

    @Test
    fun serialize() {
        val json = request.apiV1RequestSerialize()

        assertContains(json, Regex("\"name\":\\s*\"Chappy\""))
        assertContains(json, Regex("\"mode\":\\s*\"stub\""))
        assertContains(json, Regex("\"stub\":\\s*\"success\""))
        assertContains(json, Regex("\"requestType\":\\s*\"create\""))
    }

    @Test
    fun deserialize() {
        val json = request.apiV1RequestSerialize()
        val obj = json.apiV1RequestDeserialize<DogProfileCreateRequest>()

        assertEquals(request, obj)
    }
}