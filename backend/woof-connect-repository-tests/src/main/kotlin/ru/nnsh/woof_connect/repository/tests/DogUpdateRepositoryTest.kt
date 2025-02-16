package ru.nnsh.woof_connect.repository.tests

import org.junit.jupiter.api.Test
import ru.nnsh.woof_connect.common.WfcError
import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.repository.DbDogProfileRequest
import ru.nnsh.woof_connect.common.repository.IDbResponse
import ru.nnsh.woof_connect.common.repository.IDogProfileRepository
import ru.nnsh.woof_connect.common.stubs.stubDog
import ru.nnsh.woof_connect.repository.runRepoTest
import kotlin.test.assertEquals
import kotlin.test.assertIs

abstract class DogUpdateRepositoryTest(
    private val repository: IDogProfileRepository.Initializable,
    initialDogs: List<WfcDogProfileBase> = listOf(stubDog)
) {

    init {
        repository.init(initialDogs)
    }

    @Test
    fun updateSuccess() = runRepoTest {
        val updatingDog = stubDog.copy(
            description = "Old wise dog",
            age = stubDog.age + 1
        )
        val result = repository.updateDog(DbDogProfileRequest(updatingDog))
        assertIs<IDbResponse.DogProfile>(result)
        assertEquals(updatingDog, result.data)
    }

    @Test
    fun updateNotFound() = runRepoTest {
        val notFoundDog = stubDog.copy(dogId = WfcDogId(-1))
        val result = repository.updateDog(DbDogProfileRequest(notFoundDog))
        assertIs<IDbResponse.Err>(result)
        val expected = WfcError(
            code = WfcError.CODE_DB,
            field = "dogId",
            group = "database",
            message = "Dog not found"
        )
        assertEquals(expected, result.err)
    }
}
