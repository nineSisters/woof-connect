package ru.nnsh.woof_connect

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Test
import ru.nnsh.woof_connect.api.v1.models.BaseRequest
import ru.nnsh.woof_connect.api.v1.models.DogId
import ru.nnsh.woof_connect.api.v1.models.DogProfileBase
import ru.nnsh.woof_connect.api.v1.models.DogProfileCreateRequest
import ru.nnsh.woof_connect.api.v1.models.DogProfileCreateResponse
import ru.nnsh.woof_connect.api.v1.models.DogProfileDebug
import ru.nnsh.woof_connect.api.v1.models.DogProfileDeleteRequest
import ru.nnsh.woof_connect.api.v1.models.DogProfileDeleteResponse
import ru.nnsh.woof_connect.api.v1.models.DogProfileReadRequest
import ru.nnsh.woof_connect.api.v1.models.DogProfileReadResponse
import ru.nnsh.woof_connect.api.v1.models.DogProfileRequestDebugMode
import ru.nnsh.woof_connect.api.v1.models.DogProfileRequestDebugStubs
import ru.nnsh.woof_connect.api.v1.models.DogProfileUpdateRequest
import ru.nnsh.woof_connect.api.v1.models.DogProfileUpdateResponse
import ru.nnsh.woof_connect.api.v1.models.UserDogIdsRequest
import ru.nnsh.woof_connect.api.v1.models.UserDogIdsResponse
import ru.nnsh.woof_connect.api.v1.models.UserId
import ru.nnsh.woof_connect.common.stubs.stubDog
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DogProfileApiV1StubTest {

    private val stubDebug = DogProfileDebug(DogProfileRequestDebugMode.STUB, DogProfileRequestDebugStubs.SUCCESS)

    @Test
    fun testReadDogProfile() = test(
        methodName = "read",
        body = DogProfileReadRequest(
            dogId = DogId(1),
            debug = stubDebug
        )
    ) { httpResponse ->
        val response = httpResponse.body<DogProfileReadResponse>()
        assertEquals(httpResponse.status, HttpStatusCode.OK)
        assertEquals(response.dogProfile?.name, "Sharik")
    }

    @Test
    fun testCreateDogProfile() = test(
        methodName = "create",
        body = DogProfileCreateRequest(
            dogProfile = DogProfileBase(
                ownerId = UserId(1),
                dogId = DogId(0),
                name = "Bobby",
                age = 3,
                breed = "Golden Retriever",
            ),
            debug = stubDebug
        )
    ) { httpResponse ->
        val response = httpResponse.body<DogProfileCreateResponse>()
        assertEquals(httpResponse.status, HttpStatusCode.OK)
        assertTrue(response.isSuccess)
        assertEquals(response.dogId?.dogId, stubDog.dogId.id)
    }

    @Test
    fun testUpdateDogProfile() = test(
        methodName = "update",
        body = DogProfileUpdateRequest(
            dogProfile = DogProfileBase(
                dogId = DogId(11),
                ownerId = UserId(11),
                name = "Rex",
                age = 4
            ),
            debug = stubDebug
        )
    ) { httpResponse ->
        val response = httpResponse.body<DogProfileUpdateResponse>()
        assertEquals(httpResponse.status, HttpStatusCode.OK)
        assertTrue(response.isSuccess)
        assertEquals(response.dogId?.dogId, stubDog.dogId.id)
    }

    @Test
    fun testDeleteDogProfile() = test(
        methodName = "delete",
        body = DogProfileDeleteRequest(
            dogId = DogId(1),
            debug = stubDebug
        )
    ) { httpResponse ->
        val response = httpResponse.body<DogProfileDeleteResponse>()
        assertEquals(httpResponse.status, HttpStatusCode.OK)
        assertTrue(response.isSuccess)
    }

    @Test
    fun testListDogs() = test(
        methodName = "list",
        body = UserDogIdsRequest(
            userId = UserId(1),
            debug = stubDebug
        )
    ) { httpResponse ->
        val response = httpResponse.body<UserDogIdsResponse>()
        assertEquals(httpResponse.status, HttpStatusCode.OK)
        assertTrue(response.isSuccess)
        assertContentEquals(response.dogIds, listOf(DogId(1), DogId(2)))
    }

    @Test
    fun testDogProfileFail() = test(
        methodName = "list",
        body = UserDogIdsRequest(
            userId = UserId(1),
            debug = stubDebug.copy(stub = DogProfileRequestDebugStubs.NOT_FOUND)
        )
    ) { httpResponse ->
        val response = httpResponse.body<UserDogIdsResponse>()
        assertEquals(httpResponse.status, HttpStatusCode.OK)
        assertFalse(response.isSuccess)
        assertNotNull(response.error)
    }


    private fun test(
        methodName: String,
        body: BaseRequest,
        assert: suspend (HttpResponse) -> Unit
    ) = testApplication {
        application { module() }
        val client = createClient {
            install(plugin = ContentNegotiation) {
                jackson {
                    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    enable(SerializationFeature.INDENT_OUTPUT)
                    writerWithDefaultPrettyPrinter()
                }
            }
        }

        val response = client.post("/dog-profile/$methodName") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        assert(response)
    }
}
