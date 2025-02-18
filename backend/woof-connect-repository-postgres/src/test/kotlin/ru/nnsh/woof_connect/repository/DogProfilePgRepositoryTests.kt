package ru.nnsh.woof_connect.repository

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import ru.nnsh.woof_connect.repository.tests.DogCreateRepositoryTest
import ru.nnsh.woof_connect.repository.tests.DogDeleteRepositoryTest
import ru.nnsh.woof_connect.repository.tests.DogListRepositoryTest
import ru.nnsh.woof_connect.repository.tests.DogReadRepositoryTest
import ru.nnsh.woof_connect.repository.tests.DogUpdateRepositoryTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DogProfilePgRepositoryTests {

    private val repository = DogProfilePgRepository()

    @Nested
    inner class ReadTest : DogReadRepositoryTest(repository)

    @Nested
    inner class CreateTest : DogCreateRepositoryTest(repository)

    @Nested
    inner class DeleteTest : DogDeleteRepositoryTest(repository)

    @Nested
    inner class UpdateTest : DogUpdateRepositoryTest(repository)

    @Nested
    inner class ListTest : DogListRepositoryTest(repository)

    @AfterAll
    fun release() = repository.close()

    @AfterEach
    fun afterEach(): Unit = repository.deleteAll()
}
