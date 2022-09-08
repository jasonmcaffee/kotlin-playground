package com.jason.kotlinplayground.redsky.models

data class CurrentPrice(
    val value: Double,
    val currencyCode: CurrencyCode,
)