package com.jason.kotlinplayground.proxy.models

import org.postgresql.util.PGobject
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class CachedResponse(
    val url: String,
    val responseBody: String?,
    val responseHeaders: PGobject?,
    val requestBody: String?,
    val requestHeaders: PGobject,
    @Id
    val id: Long? = null,
)