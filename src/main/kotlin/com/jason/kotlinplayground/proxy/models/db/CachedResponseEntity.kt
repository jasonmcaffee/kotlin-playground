package com.jason.kotlinplayground.proxy.models.db

import org.postgresql.util.PGobject
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "cached_response")
data class CachedResponseEntity(
    val url: String,
    val responseStatusCode: Int,
    val responseBody: String?,
    val responseHeaders: PGobject?,
    val requestMethod: String,
    val requestBody: String?,
    val requestHeaders: PGobject,
    @Id
    val id: Long? = null,
)