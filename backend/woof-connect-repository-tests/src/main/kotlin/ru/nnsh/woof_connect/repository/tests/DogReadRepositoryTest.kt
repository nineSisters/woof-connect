package ru.nnsh.woof_connect.repository.tests

import org.junit.jupiter.api.Test
import ru.nnsh.woof_connect.common.WfcError
import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.repository.DbDogIdRequest
import ru.nnsh.woof_connect.common.repository.IDbResponse
import ru.nnsh.woof_connect.common.repository.IDogProfileRepository
import ru.nnsh.woof_connect.common.stubs.stubDog
import ru.nnsh.woof_connect.repository.runRepoTest
import kotlin.test.assertEquals
import kotlin.test.assertIs

abstract class DogReadRepositoryTest(
    private val repository: IDogProfileRepository.Initializable,
    initialDogs: List<WfcDogProfileBase> = listOf(stubDog)
) {

    init {
        repository.init(initialDogs)
    }

    @Test
    fun readSuccess() = runRepoTest {
        val result = repository.readDog(DbDogIdRequest(WfcDogId(1)))
        assertIs<IDbResponse.DogProfile>(result)
        assertEquals(stubDog.copy(dogId = WfcDogId(1)), result.data)
    }

    @Test
    fun readNotFound() = runRepoTest {
        val result = repository.readDog(DbDogIdRequest(WfcDogId(-1)))
        assertIs<IDbResponse.Err>(result)
        val expected = WfcError.DOG_NOT_FOUND
        assertEquals(expected, result.err)
    }
}
