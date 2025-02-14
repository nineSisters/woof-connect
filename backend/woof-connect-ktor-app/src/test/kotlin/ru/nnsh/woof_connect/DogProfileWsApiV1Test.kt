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

class DogProfileWsApiV1Test {

    @Test
    fun testReadDogProfile() = test<DogProfileReadResponse>(
        DogProfileTestFixtures.readRequest
    ) { response ->
        assertEquals(response.dogProfile?.name, "Sharik")
    }

    @Test
    fun testCreateDogProfile() = test<DogProfileCreateResponse>(
        DogProfileTestFixtures.createRequest
    ) { response ->
        assertTrue(response.isSuccess)
        assertEquals(response.dogId?.dogId, stubDog.dogId.id)
    }

    @Test
    fun testUpdateDogProfile() = test<DogProfileUpdateResponse>(
        DogProfileTestFixtures.updateRequest
    ) { response ->
        assertTrue(response.isSuccess)
        assertEquals(response.dogId?.dogId, stubDog.dogId.id)
    }

    @Test
    fun testDeleteDogProfile() = test<DogProfileDeleteResponse>(
        DogProfileTestFixtures.deleteRequest
    ) { response ->
        assertTrue(response.isSuccess)
    }

    @Test
    fun testListDogs() = test<UserDogIdsResponse>(
        DogProfileTestFixtures.listDogsRequest
    ) { response ->
        assertTrue(response.isSuccess)
        assertContentEquals(response.dogIds, listOf(DogId(1), DogId(2)))
    }

    @Test
    fun testDogProfileFail() = test<DogProfileReadResponse>(
        DogProfileTestFixtures.readRequest
            .copy(debug = DogProfileTestFixtures.notFoundStubDebug)
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