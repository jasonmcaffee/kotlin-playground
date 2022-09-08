package com.jason.kotlinplayground.redsky.redskyClient.models

data class Item(
    val productDescription: ProductDescription,
    val enrichment: Enrichment,
    val productClassification: ProductClassification,
    val primaryBrand: PrimaryBrand,
)