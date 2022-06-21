package com.jason.kotlinplayground.models.response

data class ProductResponse(
    val id: Long,
    val name: String,
    val currentPrice: Price,
)
