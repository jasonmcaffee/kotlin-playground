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

//h2 insert problems require all caps with each column named?
//@Table(name = "CACHED_RESPONSE")
//data class CachedResponseEntity(
//    @Column(value = "URL")
//    val url: String,
//    @Column(value = "RESPONSE_STATUS_CODE")
//    val responseStatusCode: Int,
//    @Column(value = "RESPONSE_BODY")
//    val responseBody: String?,
//    @Column(value = "RESPONSE_HEADERS")
//    val responseHeaders: PGobject?,
//    @Column(value = "REQUEST_METHOD")
//    val requestMethod: String,
//    @Column(value = "REQUEST_BODY")
//    val requestBody: String?,
//    @Column(value = "REQUEST_HEADERS")
//    val requestHeaders: PGobject,
//    @Id
//    @Column(value = "ID")
//    val id: Long? = null,
//)