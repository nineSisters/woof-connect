package ru.nnsh.woof_connect

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import ru.nnsh.woof_connect.repository.DogProfilePgRepository

class DogProfileApiV1PgTest : DogProfileApiV1TestBase(lazy { DogProfilePgRepository() }::value) {

    @AfterAll
    fun release() = (getTestRepository() as DogProfilePgRepository).close()

    @AfterEach
    fun afterEach(): Unit = (getTestRepository() as DogProfilePgRepository).deleteAll()
}
