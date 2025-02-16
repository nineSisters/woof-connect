package ru.nnsh.woof_connect.repository

import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.sync.Mutex
import ru.nnsh.woof_connect.common.repository.DbDogIdRequest
import ru.nnsh.woof_connect.common.repository.DbDogProfileRequest
import ru.nnsh.woof_connect.common.repository.DbOwnerIdRequest
import ru.nnsh.woof_connect.common.repository.IDbResponse
import ru.nnsh.woof_connect.common.repository.IDogProfileRepository
import io.github.reactivecircus.cache4k.Cache
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.sync.withLock
import ru.nnsh.woof_connect.common.WfcError
import ru.nnsh.woof_connect.common.repository.IDbResponse.DogProfile
import ru.nnsh.woof_connect.common.repository.IDbResponse.Err

class DogProfileInMemoryRepository(
    ttl: Duration = 1.minutes,
) : IDogProfileRepository.Initializable {

    private val currentKey = AtomicLong(1)
    private val mutex = Mutex()
    private val cache = Cache.Builder<Long, DogProfileEntity>()
        .expireAfterWrite(ttl)
        .build()

    override fun init(list: List<WfcDogProfileBase>) {
        for (dog in list) {
            val key = currentKey.getAndIncrement()
            val entity = DogProfileEntity(dog.copy(dogId = WfcDogId(key)))
            cache.put(key, entity)
        }
    }

    override suspend fun createDog(request: DbDogProfileRequest): IDbResponse<WfcDogProfileBase> = tryDogMethod {
        val key = currentKey.getAndIncrement()
        val entity = DogProfileEntity(request.dog.copy(dogId = WfcDogId(key)))
        mutex.withLock { cache.put(key, entity) }
        DogProfile(entity.toInternal())
    }

    override suspend fun readDog(request: DbDogIdRequest): IDbResponse<WfcDogProfileBase> = tryDogMethod {
        val key = request.dogId
            .takeIf { it != WfcDogId.None }
            ?: return@tryDogMethod Err(WfcError.EMPTY_DOG_ID)
        mutex.withLock { cache.get(key.id) }
            ?.toInternal()
            ?.let(::DogProfile)
            ?: Err(WfcError.DOG_NOT_FOUND)
    }

    override suspend fun updateDog(request: DbDogProfileRequest): IDbResponse<WfcDogProfileBase> = tryDogMethod {
        val key = request.dog.dogId.also { println(it.id) }
            .takeIf { it != WfcDogId.None }
            ?: return@tryDogMethod Err(WfcError.EMPTY_DOG_ID)
        mutex.withLock {
            cache.get(key.id) ?: return@tryDogMethod Err(WfcError.DOG_NOT_FOUND)
            cache.put(key.id, DogProfileEntity(request.dog))
            DogProfile(request.dog.copy())
        }
    }

    override suspend fun deleteDog(request: DbDogIdRequest): IDbResponse<WfcDogProfileBase> = tryDogMethod {
        val key = request.dogId
            .takeIf { it != WfcDogId.None }
            ?: return@tryDogMethod Err(WfcError.EMPTY_DOG_ID)
        mutex.withLock {
            val deletedDog = cache.get(key.id) ?: return@tryDogMethod Err(WfcError.DOG_NOT_FOUND)
            cache.invalidate(key.id)
            DogProfile(deletedDog.toInternal())
        }
    }

    override suspend fun listDogs(request: DbOwnerIdRequest): IDbResponse<List<WfcDogId>> = tryDogMethod {
        val list = cache.asMap().values.asSequence()
            .map { it.toInternal() }
            .filter { it.ownerId == request.ownerId }
            .map { it.dogId }
            .toList()
        IDbResponse.DogIds(list)
    }
}
