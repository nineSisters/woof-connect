package ru.nnsh.woof_connect.common.repository

import org.jetbrains.annotations.TestOnly
import ru.nnsh.woof_connect.common.WfcError
import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase

interface IDogProfileRepository {
    suspend fun createDog(request: DbDogProfileRequest): IDbResponse<WfcDogProfileBase>
    suspend fun readDog(request: DbDogIdRequest): IDbResponse<WfcDogProfileBase>
    suspend fun updateDog(request: DbDogProfileRequest): IDbResponse<WfcDogProfileBase>
    suspend fun deleteDog(request: DbDogIdRequest): IDbResponse<WfcDogProfileBase>
    suspend fun listDogs(request: DbOwnerIdRequest): IDbResponse<List<WfcDogId>>

    companion object NONE : IDogProfileRepository {
        private fun notImplemented(): Nothing = throw NotImplementedError("Must not be used")
        override suspend fun createDog(request: DbDogProfileRequest): IDbResponse<WfcDogProfileBase> = notImplemented()
        override suspend fun readDog(request: DbDogIdRequest): IDbResponse<WfcDogProfileBase> = notImplemented()
        override suspend fun updateDog(request: DbDogProfileRequest): IDbResponse<WfcDogProfileBase> = notImplemented()
        override suspend fun deleteDog(request: DbDogIdRequest): IDbResponse<WfcDogProfileBase> = notImplemented()
        override suspend fun listDogs(request: DbOwnerIdRequest): IDbResponse<List<WfcDogId>> = notImplemented()
    }

    suspend fun <T> IDogProfileRepository.tryDogMethod(block: suspend () -> IDbResponse<T>): IDbResponse<T> = try {
        block()
    } catch (e: Exception) {
        IDbResponse.Err(
            WfcError(
                WfcError.CODE_DB,
                message = "method exception",
                cause = e
            )
        )
    }

    interface Initializable : IDogProfileRepository {
        @TestOnly
        fun init(list: List<WfcDogProfileBase>)
    }
}
