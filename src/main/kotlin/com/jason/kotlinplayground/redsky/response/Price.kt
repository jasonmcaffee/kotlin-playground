package com.jason.kotlinplayground.redsky.models.response

import com.jason.kotlinplayground.redsky.models.CurrencyCode

data class Price(
    val value: Double,
    val currencyCode: CurrencyCode,
)