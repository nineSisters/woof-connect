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
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import ru.nnsh.woof_connect.api.v1.models.BaseRequest
import ru.nnsh.woof_connect.api.v1.models.DogId
import ru.nnsh.woof_connect.api.v1.models.DogProfileCreateResponse
import ru.nnsh.woof_connect.api.v1.models.DogProfileDeleteResponse
import ru.nnsh.woof_connect.api.v1.models.DogProfileReadResponse
import ru.nnsh.woof_connect.api.v1.models.DogProfileRequestDebugMode
import ru.nnsh.woof_connect.api.v1.models.DogProfileUpdateResponse
import ru.nnsh.woof_connect.api.v1.models.UserDogIdsResponse
import ru.nnsh.woof_connect.common.WfcCorConfiguration
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.dog_profile.WfcOwnerId
import ru.nnsh.woof_connect.common.repository.IDogProfileRepository
import ru.nnsh.woof_connect.common.stubs.stubDog
import ru.nnsh.woof_connect.fixtures.DogProfileTestFixtures
import ru.nnsh.woof_connect.logger.loggerFactory

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class DogProfileApiV1TestBase(
    private val getTestRepository: () -> IDogProfileRepository.Initializable
) {

    private val fixtures = DogProfileTestFixtures(DogProfileRequestDebugMode.TEST)

    private fun getWfcCorConfiguration(
        initialDogs: List<WfcDogProfileBase>
    ) = WfcCorConfiguration(
        loggerFactory,
        testRepository = getTestRepository().apply { init(initialDogs) }
    )

    @Test
    fun testReadDogProfile() = test(
        methodName = "read",
        body = fixtures.readRequest,
        initialDogs = listOf(stubDog)
    ) { httpResponse ->
        val response = httpResponse.body<DogProfileReadResponse>()
        assertEquals(HttpStatusCode.OK, httpResponse.status)
        assertEquals("Sharik", response.dogProfile?.name)
    }

    @Test
    fun testCreateDogProfile() = test(
        methodName = "create",
        body = fixtures.createRequest,
        initialDogs = listOf(stubDog)
    ) { httpResponse ->
        val response = httpResponse.body<DogProfileCreateResponse>()
        assertEquals(HttpStatusCode.OK, httpResponse.status)
        assertTrue(response.isSuccess)
        assertEquals(2, response.dogId?.dogId)

    }

    @Test
    fun testUpdateDogProfile() = test(
        methodName = "update",
        body = fixtures.updateRequest,
        initialDogs = listOf(stubDog)
    ) { httpResponse ->
        val response = httpResponse.body<DogProfileUpdateResponse>()
        assertEquals(httpResponse.status, HttpStatusCode.OK)
        assertTrue(response.isSuccess)
        assertEquals(1, response.dogProfile?.dogId?.dogId)
        assertEquals("Rex", response.dogProfile?.name)
        assertEquals("Mongrel", response.dogProfile?.breed)
        assertEquals(fixtures.updateRequest.dogProfile.age, response.dogProfile?.age)
        assertEquals(stubDog.description, response.dogProfile?.description)
    }

    @Test
    fun testDeleteDogProfile() = test(
        methodName = "delete",
        body = fixtures.deleteRequest,
        initialDogs = listOf(stubDog)
    ) { httpResponse ->
        val response = httpResponse.body<DogProfileDeleteResponse>()
        assertEquals(httpResponse.status, HttpStatusCode.OK)
        assertTrue(response.isSuccess)
    }

    @Test
    fun testListDogs() = test(
        methodName = "list",
        body = fixtures.listDogsRequest,
        initialDogs = List(10) { stubDog.copy(ownerId = WfcOwnerId.TheOne) }
    ) { httpResponse ->
        val response = httpResponse.body<UserDogIdsResponse>()
        assertEquals(httpResponse.status, HttpStatusCode.OK)
        assertTrue(response.isSuccess)
        assertContentEquals(response.dogIds, List(10) { DogId(it.toLong().inc()) })
    }


    private fun test(
        methodName: String,
        body: BaseRequest,
        initialDogs: List<WfcDogProfileBase>,
        assert: suspend (HttpResponse) -> Unit
    ) = testApplication {
        application { module(getWfcCorConfiguration(initialDogs)) }
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
