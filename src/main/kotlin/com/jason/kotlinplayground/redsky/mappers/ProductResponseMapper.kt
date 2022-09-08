package com.jason.kotlinplayground.redsky.mappers

import com.jason.kotlinplayground.redsky.models.ProductPricing
import com.jason.kotlinplayground.redsky.models.response.ProductResponse
import com.jason.kotlinplayground.redsky.models.response.Price
import com.jason.kotlinplayground.redsky.redskyClient.models.ProductInfoResponse

class ProductResponseMapper {
    companion object{
        fun fromProductInfoAndProductPricing(productInfoResponse: ProductInfoResponse, productPricing: ProductPricing): ProductResponse {
            return ProductResponse(
                id = productInfoResponse.data.product.tcin,
                name = productInfoResponse.data.product.item.productDescription.title,
                currentPrice = Price(productPricing.currentPrice.value, productPricing.currentPrice.currencyCode)
            )
        }
    }
}