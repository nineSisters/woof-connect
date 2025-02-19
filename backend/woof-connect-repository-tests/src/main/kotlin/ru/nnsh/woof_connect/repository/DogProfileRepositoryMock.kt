package ru.nnsh.woof_connect.repository

import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.repository.*

open class DogProfileRepositoryMock : IDogProfileRepository, IDogProfileRepository.Initializable {

    protected open val defaultDogProfileResponse = IDbResponse.DogProfile(WfcDogProfileBase())
    protected open val defaultDogListResponse = IDbResponse.DogIds(emptyList())

    override suspend fun createDog(request: DbDogProfileRequest): IDbResponse<WfcDogProfileBase> {
        return defaultDogProfileResponse
    }

    override suspend fun readDog(request: DbDogIdRequest): IDbResponse<WfcDogProfileBase> {
        return defaultDogProfileResponse
    }

    override suspend fun updateDog(request: DbDogProfileRequest): IDbResponse<WfcDogProfileBase> {
        return defaultDogProfileResponse
    }

    override suspend fun deleteDog(request: DbDogIdRequest): IDbResponse<WfcDogProfileBase> {
        return defaultDogProfileResponse
    }

    override suspend fun listDogs(request: DbOwnerIdRequest): IDbResponse<List<WfcDogId>> {
        return defaultDogListResponse
    }

    override fun init(list: List<WfcDogProfileBase>) = Unit
}
