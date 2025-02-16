package ru.nnsh.woof_connect.repository

import org.junit.jupiter.api.Nested
import ru.nnsh.woof_connect.repository.tests.DogCreateRepositoryTest
import ru.nnsh.woof_connect.repository.tests.DogDeleteRepositoryTest
import ru.nnsh.woof_connect.repository.tests.DogListRepositoryTest
import ru.nnsh.woof_connect.repository.tests.DogReadRepositoryTest
import ru.nnsh.woof_connect.repository.tests.DogUpdateRepositoryTest

class DogProfileInMemoryRepositoryTests {

    private val repository get() = DogProfileInMemoryRepository()

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
}
