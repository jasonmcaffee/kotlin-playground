package com.jason.kotlinplayground.redskyClient.models

data class Item(
    val productDescription: ProductDescription,
    val enrichment: Enrichment,
    val productClassification: ProductClassification,
    val primaryBrand: PrimaryBrand,
)