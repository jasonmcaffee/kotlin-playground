package com.jason.kotlinplayground.redsky.models.response

data class ProductResponse(
    val id: Long,
    val name: String,
    val currentPrice: Price,
)
