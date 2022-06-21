package com.jason.kotlinplayground.mappers

import com.jason.kotlinplayground.models.ProductPricing
import com.jason.kotlinplayground.models.response.ProductResponse
import com.jason.kotlinplayground.models.response.Price
import com.jason.kotlinplayground.redskyClient.models.ProductInfoResponse

class ProductResponseMapper {
    companion object{
        fun fromProductInfoAndProductPricing(productInfoResponse: ProductInfoResponse, productPricing: ProductPricing): ProductResponse{
            return ProductResponse(
                id = productInfoResponse.data.product.tcin,
                name = productInfoResponse.data.product.item.productDescription.title,
                currentPrice = Price(productPricing.currentPrice.value, productPricing.currentPrice.currencyCode)
            )
        }
    }
}