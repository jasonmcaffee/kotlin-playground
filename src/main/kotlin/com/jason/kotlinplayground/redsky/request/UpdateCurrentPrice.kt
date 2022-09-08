package com.jason.kotlinplayground.redsky.request

import com.jason.kotlinplayground.redsky.models.CurrencyCode
import javax.validation.constraints.Min

data class UpdateCurrentPrice (
    @field:Min(value=0, message = "Price must not be less than 0")
    val value: Double,
    val currencyCode: CurrencyCode,
)