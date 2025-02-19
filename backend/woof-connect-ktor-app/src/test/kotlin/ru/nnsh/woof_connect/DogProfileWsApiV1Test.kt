package ru.nnsh.woof_connect

import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.serialization.jackson.JacksonWebsocketContentConverter
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Test
import ru.nnsh.woof_connect.api.v1.models.BaseRequest
import ru.nnsh.woof_connect.api.v1.models.BaseResponse
import ru.nnsh.woof_connect.api.v1.models.DogId
import ru.nnsh.woof_connect.api.v1.models.DogProfileCreateResponse
import ru.nnsh.woof_connect.api.v1.models.DogProfileDeleteResponse
import ru.nnsh.woof_connect.api.v1.models.DogProfileReadResponse
import ru.nnsh.woof_connect.api.v1.models.DogProfileUpdateResponse
import ru.nnsh.woof_connect.api.v1.models.SessionInit
import ru.nnsh.woof_connect.api.v1.models.UserDogIdsResponse
import ru.nnsh.woof_connect.common.stubs.stubDog
import ru.nnsh.woof_connect.fixtures.DogProfileTestFixtures
import ru.nnsh.woof_connect.serializer.apiV1ObjectMapper
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import ru.nnsh.woof_connect.api.v1.models.DogProfileRequestDebugMode

class DogProfileWsApiV1Test {

    private val fixtures = DogProfileTestFixtures(DogProfileRequestDebugMode.STUB)

    @Test
    fun testReadDogProfile() = test<DogProfileReadResponse>(
        fixtures.readRequest
    ) { response ->
        assertEquals(response.dogProfile?.name, "Sharik")
    }

    @Test
    fun testCreateDogProfile() = test<DogProfileCreateResponse>(
        fixtures.createRequest
    ) { response ->
        assertTrue(response.isSuccess)
        assertEquals(response.dogId?.dogId, stubDog.dogId.id)
    }

    @Test
    fun testUpdateDogProfile() = test<DogProfileUpdateResponse>(
        fixtures.updateRequest
    ) { response ->
        assertTrue(response.isSuccess)
        assertEquals(1, response.dogProfile?.dogId?.dogId)
        assertEquals("Rex", response.dogProfile?.name)
        assertEquals(4, response.dogProfile?.age)
    }

    @Test
    fun testDeleteDogProfile() = test<DogProfileDeleteResponse>(
        fixtures.deleteRequest
    ) { response ->
        assertTrue(response.isSuccess)
    }

    @Test
    fun testListDogs() = test<UserDogIdsResponse>(
        fixtures.listDogsRequest
    ) { response ->
        assertTrue(response.isSuccess)
        assertContentEquals(List(10) { DogId(it.inc().toLong()) }, response.dogIds)
    }

    @Test
    fun testDogProfileFail() = test<DogProfileReadResponse>(
        fixtures.readRequest
            .copy(debug = fixtures.notFoundStubDebug)
    ) { response ->
        assertFalse(response.isSuccess)
        assertNotNull(response.error)
    }


    private inline fun <reified T : BaseResponse> test(
        request: BaseRequest,
        crossinline assert: (T) -> Unit
    ) {
        testApplication {
            application { module() }
            val client = createClient {
                install(WebSockets) {
                    contentConverter = JacksonWebsocketContentConverter(apiV1ObjectMapper)
                }
            }
            client.webSocket("/ws") {
                withTimeout(1.seconds) {
                    receiveDeserialized<SessionInit>()
                    sendSerialized(request)
                    val response = receiveDeserialized<T>()
                    assert(response)
                }
            }
        }
    }
}
