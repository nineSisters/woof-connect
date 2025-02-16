package repository

import kotlinx.coroutines.test.runTest
import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.dog_profile.WfcOwnerId
import ru.nnsh.woof_connect.common.repository.DbDogProfileRequest
import ru.nnsh.woof_connect.common.repository.DbOwnerIdRequest
import ru.nnsh.woof_connect.common.repository.IDbResponse
import ru.nnsh.woof_connect.common.repository.IDogProfileRepository
import ru.nnsh.woof_connect.common.stubs.stubDog
import ru.nnsh.woof_connect.repository.DogProfileRepositoryMock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MockRepositoryTest {

    private val repository: IDogProfileRepository = object : DogProfileRepositoryMock() {
        override val defaultDogProfileResponse = IDbResponse.DogProfile(stubDog)
        override val defaultDogListResponse = IDbResponse.DogIds(List(10) { WfcDogId(it.toLong()) })
    }

    @Test
    fun mockCreate() = runTest {
        val result = repository.createDog(DbDogProfileRequest(WfcDogProfileBase()))
        assertIs<IDbResponse<WfcDogProfileBase>>(result)
        assertEquals(stubDog, result.data)
    }

    @Test
    fun mockListDogs() = runTest {
        val result = repository.listDogs(DbOwnerIdRequest(WfcOwnerId.None))
        assertIs<IDbResponse<List<WfcDogId>>>(result)
        assertEquals(List(10) { WfcDogId(it) }, result.data)
    }
}
