package ru.nnsh.woof_connect.serializer

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import ru.nnsh.woof_connect.api.v1.models.BaseRequest
import ru.nnsh.woof_connect.api.v1.models.BaseResponse


private val kotlinModule = KotlinModule.Builder()
    .enable(KotlinFeature.NullIsSameAsDefault)
    .enable(KotlinFeature.SingletonSupport)
    .build()

val apiV1ObjectMapper: JsonMapper = JsonMapper.builder()
    .addModules(kotlinModule)
    .enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
    .build()

internal fun BaseResponse.apiV1ResponseSerialize() = apiV1ObjectMapper.writeValueAsString(this)
internal inline fun <reified T: BaseResponse> String.apiV1ResponseDeserialize() = apiV1ObjectMapper.readValue(this, T::class.java) as T

internal fun BaseRequest.apiV1RequestSerialize() = apiV1ObjectMapper.writeValueAsString(this)
internal inline fun <reified T: BaseRequest> String.apiV1RequestDeserialize() = apiV1ObjectMapper.readValue(this, T::class.java) as T

