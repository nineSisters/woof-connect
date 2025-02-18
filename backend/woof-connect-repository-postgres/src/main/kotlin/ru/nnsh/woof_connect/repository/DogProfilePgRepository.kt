package ru.nnsh.woof_connect.repository

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.nnsh.woof_connect.common.WfcError
import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.repository.DbDogIdRequest
import ru.nnsh.woof_connect.common.repository.DbDogProfileRequest
import ru.nnsh.woof_connect.common.repository.DbOwnerIdRequest
import ru.nnsh.woof_connect.common.repository.IDbResponse
import ru.nnsh.woof_connect.common.repository.IDbResponse.DogProfile
import ru.nnsh.woof_connect.common.repository.IDbResponse.Err
import ru.nnsh.woof_connect.common.repository.IDogProfileRepository
import ru.nnsh.woof_connect.postgress.DogProfileTable
import ru.nnsh.woof_connect.postgress.PgProperties

class DogProfilePgRepository(
    pgProperties: PgProperties = PgProperties()
) : IDogProfileRepository.Initializable, AutoCloseable {
    private val hikariConfig = HikariConfig().apply {
        jdbcUrl = pgProperties.url
        driverClassName = "org.postgresql.Driver"
        username = pgProperties.user
        password = pgProperties.password
        maximumPoolSize = 5
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        keepaliveTime = 30_000
        validate()
    }

    private val source = HikariDataSource(hikariConfig)

    private val connection by lazy {
        Database.connect(
            datasource = HikariDataSource(hikariConfig)
        ).also {
            transaction(it) {
                if (!dogTable.exists()) {
                    SchemaUtils.create(dogTable)
                }
            }
        }
    }

    private val dogTable = DogProfileTable(schema = pgProperties.schema)

    init {

    }

    override fun init(list: List<WfcDogProfileBase>): Unit = transaction(connection) {
        dogTable.batchInsert(list) { dog ->
            dogTable.toStorage(this, dog.copy(dogId = WfcDogId.None))
        }
    }

    override suspend fun createDog(request: DbDogProfileRequest): IDbResponse<WfcDogProfileBase> = tryTransaction {
        val row = dogTable.insert { dogTable.toStorage(it, request.dog) }
            .resultedValues?.firstOrNull()
        if (row == null) {
            Err(WfcError(WfcError.CODE_DB, message = "Failed to save dog"))
        } else {
            DogProfile(dogTable.toInternal(row))
        }
    }

    override suspend fun readDog(request: DbDogIdRequest): IDbResponse<WfcDogProfileBase> = tryTransaction {
        val row = dogTable.selectAll().where {
            dogTable.id eq request.dogId.id
        }.singleOrNull()

        if (row == null) {
            Err(WfcError.DOG_NOT_FOUND)
        } else {
            DogProfile(dogTable.toInternal(row))
        }
    }

    override suspend fun updateDog(request: DbDogProfileRequest): IDbResponse<WfcDogProfileBase> = tryTransaction {
        if (request.dog.dogId == WfcDogId.None) return@tryTransaction Err(WfcError.EMPTY_DOG_ID)
        val predicate = dogTable.id eq request.dog.dogId.id
        val current = dogTable.selectAll().where(predicate)
            .singleOrNull()
            ?.let(dogTable::toInternal) ?: return@tryTransaction Err(WfcError.DOG_NOT_FOUND)

        // TODO implement optimistic locking

        dogTable.update({ predicate }) { dogTable.toStorage(it, request.dog) }
        DogProfile(dogTable.toInternal(dogTable.selectAll().where(predicate).single()))
    }

    override suspend fun deleteDog(request: DbDogIdRequest): IDbResponse<WfcDogProfileBase> = tryTransaction {
        if (request.dogId == WfcDogId.None) return@tryTransaction Err(WfcError.EMPTY_DOG_ID)
        val predicate = dogTable.id eq request.dogId.id
        val current = dogTable.selectAll().where(predicate)
            .singleOrNull()
            ?.let(dogTable::toInternal) ?: return@tryTransaction Err(WfcError.DOG_NOT_FOUND)
        dogTable.deleteWhere { predicate }
        DogProfile(current)

    }

    override suspend fun listDogs(request: DbOwnerIdRequest): IDbResponse<List<WfcDogId>> = tryTransaction {

        val result = dogTable.select(dogTable.id).where { dogTable.ownerId eq request.ownerId.id }
            .toList()
            .map { WfcDogId(it[dogTable.id].value) }
        IDbResponse.DogIds(result)
    }

    fun deleteAll(): Unit = transaction(connection) {
        dogTable.deleteAll()
        exec("TRUNCATE TABLE ${dogTable.tableName} RESTART IDENTITY;")
    }

    override fun close() = source.close()

    private suspend inline fun <T> tryTransaction(crossinline block: suspend () -> IDbResponse<T>): IDbResponse<T> {
        return newSuspendedTransaction(Dispatchers.IO, connection) {
            try {
                block()
            } catch (e: Exception) {
                Err(
                    WfcError(
                        WfcError.CODE_DB,
                        message = "sql exception",
                        cause = e
                    )
                )
            }
        }
    }
}
