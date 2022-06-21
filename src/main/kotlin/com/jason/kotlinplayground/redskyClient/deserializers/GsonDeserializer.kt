package com.jason.kotlinplayground.redskyClient.deserializers

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.jason.kotlinplayground.redskyClient.models.ProductInfoResponse

class GsonDeserializer: ResponseDeserializable<ProductInfoResponse> {
    override fun deserialize(content: String): ProductInfoResponse? {
        return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
            .fromJson(content, ProductInfoResponse::class.java)
    }
}