package ru.nnsh.woof_connect.common.repository

import ru.nnsh.woof_connect.common.WfcError
import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase

sealed interface IDbResponse<out T> {
    val data: T

    data class DogProfile(override val data: WfcDogProfileBase) : IDbResponse<WfcDogProfileBase>
    data class DogIds(override val data: List<WfcDogId>) : IDbResponse<List<WfcDogId>>
    data class Err(val err: WfcError) : IDbResponse<Nothing> {
        override val data: Nothing get() = error("No data in err response")
    }
}
