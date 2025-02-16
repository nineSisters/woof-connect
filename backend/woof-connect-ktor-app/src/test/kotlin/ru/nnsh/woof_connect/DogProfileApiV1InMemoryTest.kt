package ru.nnsh.woof_connect

import ru.nnsh.woof_connect.repository.DogProfileInMemoryRepository

class DogProfileApiV1InMemoryTest : DogProfileApiV1TestBase(::DogProfileInMemoryRepository)
