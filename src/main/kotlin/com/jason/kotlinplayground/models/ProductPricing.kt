package com.jason.kotlinplayground.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("productPricing")
data class ProductPricing(
    @Id
    val id: Long,
    val currentPrice: CurrentPrice
)


