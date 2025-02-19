package ru.nnsh.woof_connect.repository.tests

import kotlin.test.assertContentEquals
import kotlin.test.assertIs
import org.junit.jupiter.api.Test
import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.repository.DbDogProfileRequest
import ru.nnsh.woof_connect.common.repository.DbOwnerIdRequest
import ru.nnsh.woof_connect.common.repository.IDbResponse
import ru.nnsh.woof_connect.common.repository.IDogProfileRepository
import ru.nnsh.woof_connect.common.stubs.stubDog
import ru.nnsh.woof_connect.repository.runRepoTest

abstract class DogListRepositoryTest(
    private val repository: IDogProfileRepository.Initializable,
    initialDogs: List<WfcDogProfileBase> = List(10) { stubDog.copy(dogId = WfcDogId(it.inc())) }
) {

    init {
        repository.init(initialDogs)
    }

    @Test
    fun listDogsSuccess() = runRepoTest {
        val result = repository.listDogs(DbOwnerIdRequest(stubDog.ownerId))
        assertIs<IDbResponse.DogIds>(result)
        assertContentEquals(List(10) { WfcDogId(it.inc()) }, result.data)
    }
}
