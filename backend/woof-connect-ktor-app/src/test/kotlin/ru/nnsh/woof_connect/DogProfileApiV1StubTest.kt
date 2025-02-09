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
import ru.nnsh.woof_connect.api.v1.models.DogProfileCreateResponse
import ru.nnsh.woof_connect.api.v1.models.DogProfileDeleteResponse
import ru.nnsh.woof_connect.api.v1.models.DogProfileReadResponse
import ru.nnsh.woof_connect.api.v1.models.DogProfileUpdateResponse
import ru.nnsh.woof_connect.api.v1.models.UserDogIdsResponse
import ru.nnsh.woof_connect.common.stubs.stubDog
import ru.nnsh.woof_connect.fixtures.DogProfileTestFixtures
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DogProfileApiV1StubTest {

    @Test
    fun testReadDogProfile() = test(
        methodName = "read",
        body = DogProfileTestFixtures.readRequest
    ) { httpResponse ->
        val response = httpResponse.body<DogProfileReadResponse>()
        assertEquals(HttpStatusCode.OK, httpResponse.status)
        assertEquals("Sharik", response.dogProfile?.name)
    }

    @Test
    fun testCreateDogProfile() = test(
        methodName = "create",
        body = DogProfileTestFixtures.createRequest
    ) { httpResponse ->
        val response = httpResponse.body<DogProfileCreateResponse>()
        assertEquals(HttpStatusCode.OK, httpResponse.status)
        assertTrue(response.isSuccess)
        assertEquals(stubDog.dogId.id, response.dogId?.dogId)
    }

    @Test
    fun testUpdateDogProfile() = test(
        methodName = "update",
        body = DogProfileTestFixtures.updateRequest
    ) { httpResponse ->
        val response = httpResponse.body<DogProfileUpdateResponse>()
        assertEquals(httpResponse.status, HttpStatusCode.OK)
        assertTrue(response.isSuccess)
        assertEquals(response.dogId?.dogId, stubDog.dogId.id)
    }

    @Test
    fun testDeleteDogProfile() = test(
        methodName = "delete",
        body = DogProfileTestFixtures.deleteRequest
    ) { httpResponse ->
        val response = httpResponse.body<DogProfileDeleteResponse>()
        assertEquals(httpResponse.status, HttpStatusCode.OK)
        assertTrue(response.isSuccess)
    }

    @Test
    fun testListDogs() = test(
        methodName = "list",
        body = DogProfileTestFixtures.listDogsRequest
    ) { httpResponse ->
        val response = httpResponse.body<UserDogIdsResponse>()
        assertEquals(httpResponse.status, HttpStatusCode.OK)
        assertTrue(response.isSuccess)
        assertContentEquals(response.dogIds, LongArray(10) { it.toLong() }.map { DogId(it) })
    }

    @Test
    fun testDogProfileFail() = test(
        methodName = "list",
        body = DogProfileTestFixtures.listDogsRequest
            .copy(debug = DogProfileTestFixtures.notFoundStubDebug)
    ) { httpResponse ->
        val response = httpResponse.body<UserDogIdsResponse>()
        assertEquals(HttpStatusCode.OK, httpResponse.status)
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
