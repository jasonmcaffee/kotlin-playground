package com.jason.kotlinplayground.redsky.request

import javax.validation.Valid

data class UpdateProductRequest (
    val name: String,//weird to allow name to be provided but not updatable.
    @field:Valid
    val currentPrice: UpdateCurrentPrice,
)