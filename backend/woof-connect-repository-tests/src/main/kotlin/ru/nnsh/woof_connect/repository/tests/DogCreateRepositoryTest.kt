package ru.nnsh.woof_connect.repository.tests

import org.junit.jupiter.api.Test
import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.repository.DbDogProfileRequest
import ru.nnsh.woof_connect.common.repository.IDbResponse
import ru.nnsh.woof_connect.common.repository.IDogProfileRepository
import ru.nnsh.woof_connect.common.stubs.stubDog
import ru.nnsh.woof_connect.repository.runRepoTest
import kotlin.test.assertEquals
import kotlin.test.assertIs

abstract class DogCreateRepositoryTest(
    private val repository: IDogProfileRepository.Initializable,
    initialDogs: List<WfcDogProfileBase> = emptyList()
) {

    init {
        repository.init(initialDogs)
    }

    private val dogToCreate = stubDog.copy(dogId = WfcDogId.None)

    @Test
    fun createSuccess() = runRepoTest {
        val result = repository.createDog(DbDogProfileRequest(dogToCreate))
        val expected = dogToCreate.copy(dogId = WfcDogId(1))
        assertIs<IDbResponse.DogProfile>(result)
        assertEquals(expected, result.data)
    }
}
