package com.jason.kotlinplayground.models.response

import com.jason.kotlinplayground.models.CurrencyCode

data class Price(
    val value: Double,
    val currencyCode: CurrencyCode,
)