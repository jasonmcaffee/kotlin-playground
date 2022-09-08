package com.jason.kotlinplayground.redsky.mappers

import com.jason.kotlinplayground.redsky.models.CurrentPrice
import com.jason.kotlinplayground.redsky.models.ProductPricing
import com.jason.kotlinplayground.redsky.request.UpdateProductRequest

class ProductRequestMapper {
    companion object {
        fun toProductPricing(id: Long, updateProductRequest: UpdateProductRequest) : ProductPricing {
            return ProductPricing(id,
                CurrentPrice(updateProductRequest.currentPrice.value, updateProductRequest.currentPrice.currencyCode)
            )
        }
    }
}