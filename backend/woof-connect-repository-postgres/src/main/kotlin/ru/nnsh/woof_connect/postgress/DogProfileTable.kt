package ru.nnsh.woof_connect.postgress

import java.net.URI
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.dog_profile.WfcOwnerId

class DogProfileTable(schema: String) : LongIdTable(
    name = "$schema.$TABLE_NAME",
    columnName = DOG_ID
) {
    val ownerId = long(OWNER_ID).default(0).index()
    val name = text(NAME).default("")
    val breed = text(BREED).nullable().default(null)
    val age = integer(AGE).default(-1)
    val weight = float(WEIGHT).nullable().default(null)
    val description = mediumText(DESCRIPTION).nullable().default(null)
    val photoUrl = text(PHOTO_URL).nullable().default(null)

    companion object Fields {
        const val TABLE_NAME = "dog_profile_table"

        const val DOG_ID = "dog_id"
        const val OWNER_ID = "owner_id"
        const val NAME = "name"
        const val BREED = "breed"
        const val PHOTO_URL = "photo_url"
        const val AGE = "age"
        const val WEIGHT = "weight"
        const val DESCRIPTION = "description"
    }

    fun toInternal(result: ResultRow) = WfcDogProfileBase(
        dogId = WfcDogId(result[id].value),
        ownerId = WfcOwnerId(result[ownerId]),
        name = result[name],
        breed = result[breed],
        age = result[age],
        weight = result[weight] ?: Float.NaN,
        description = result[description],
        photoUrl = result[photoUrl]?.let { URI.create(it) }
    )

    fun toStorage(statement: UpdateBuilder<*>, dog: WfcDogProfileBase) {
        if (dog.dogId != WfcDogId.None) {
            statement[id] = dog.dogId.id
        }
        statement[name] = dog.name
        statement[ownerId] = dog.ownerId.id
        statement[breed] = dog.breed
        statement[description] = dog.description
        statement[age] = dog.age
        statement[weight] = dog.weight
        statement[photoUrl] = dog.photoUrl?.toString()
    }
}
