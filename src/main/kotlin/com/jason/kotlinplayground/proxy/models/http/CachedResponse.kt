package com.jason.kotlinplayground.proxy.models.http

data class CachedResponse(
    val url: String,
    val responseStatusCode: Int,
    val responseBody: String?,
    val responseHeaders: Map<String, List<String>>?,
    val requestMethod: String,
    val requestBody: String?,
    val requestHeaders: Map<String, String>,
    val id: Long,
)