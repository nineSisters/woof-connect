package ru.nnsh.woof_connect.common.stubs

import java.net.URI
import ru.nnsh.woof_connect.common.dog_profile.WfcDogId
import ru.nnsh.woof_connect.common.dog_profile.WfcDogProfileBase
import ru.nnsh.woof_connect.common.dog_profile.WfcOwnerId

val stubDog = WfcDogProfileBase(
    WfcOwnerId(11),
    WfcDogId(11),
    "Sharik",
    "Mongrel",
    3,
    22.5f,
    "Happy dog",
    URI.create("https://upload.wikimedia.org/wikipedia/commons/d/d5/Retriever_in_water.jpg")
)