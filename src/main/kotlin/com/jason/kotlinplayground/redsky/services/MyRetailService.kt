package com.jason.kotlinplayground.redsky.services

import com.jason.kotlinplayground.redsky.mappers.ProductRequestMapper
import com.jason.kotlinplayground.redsky.mappers.ProductResponseMapper
import com.jason.kotlinplayground.redsky.models.exceptions.ProductPricingNotFoundException
import com.jason.kotlinplayground.redsky.request.UpdateProductRequest
import com.jason.kotlinplayground.redsky.models.response.ProductResponse
import com.jason.kotlinplayground.redsky.redskyClient.RedSkyClient
import com.jason.kotlinplayground.redsky.repositories.ProductPricingRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service

@Service
class MyRetailService(
    private val productPricingRepository: ProductPricingRepository,
    private val redSkyClient: RedSkyClient,
) {
    /**
     * Fetch product info from redsky and product pricing info from the db, in parallel.
     * If either call fails, throw appropriate exception
     */
    suspend fun getProductById(id: Long): ProductResponse = supervisorScope{
        //get data in parallel
        val productInfoPromise = async { redSkyClient.getProductInfo(id) }
        val productPricingPromise = async { productPricingRepository.getProductPricingById(id) }
        val productPricing = try {
            productPricingPromise.await()
        } catch(e: Exception){
            when(e){
                is EmptyResultDataAccessException -> throw ProductPricingNotFoundException("no pricing found for product id: $id")
                else -> throw e
            }
        }
        val productInfo = productInfoPromise.await()
        return@supervisorScope ProductResponseMapper.fromProductInfoAndProductPricing(productInfo, productPricing)
    }

    /**
     * Update product pricing in db.
     */
    fun updateProductPrice(id: Long, updateProductRequest: UpdateProductRequest){
        //ensure the document already exists.
        try {
            productPricingRepository.getProductPricingById(id)
        } catch(e: Exception){
            when(e){
                is EmptyResultDataAccessException -> throw ProductPricingNotFoundException("no pricing found for product id: $id")
                else -> throw e
            }
        }

        val currentPrice = ProductRequestMapper.toProductPricing(id, updateProductRequest)
        productPricingRepository.save(currentPrice)
    }
}