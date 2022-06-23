package com.jason.kotlinplayground.mappers

import com.jason.kotlinplayground.models.CurrentPrice
import com.jason.kotlinplayground.models.ProductPricing
import com.jason.kotlinplayground.models.request.UpdateProductRequest

class ProductRequestMapper {
    companion object {
        fun toProductPricing(id: Long, updateProductRequest: UpdateProductRequest) : ProductPricing{
            return ProductPricing(id,
                CurrentPrice(updateProductRequest.currentPrice.value, updateProductRequest.currentPrice.currencyCode))
        }
    }
}