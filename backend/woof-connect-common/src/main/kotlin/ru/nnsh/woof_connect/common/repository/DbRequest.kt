package ru.nnsh.woof_connect.common.repository

import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.dog_profile.WfcOwnerId

data class DbDogIdRequest(val dogId: WfcDogId)

data class DbDogProfileRequest(val dog: WfcDogProfileBase)

data class DbOwnerIdRequest(val ownerId: WfcOwnerId)
