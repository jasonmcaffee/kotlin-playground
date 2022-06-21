package com.jason.kotlinplayground.repositories

import com.jason.kotlinplayground.models.ProductPricing
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProductPricingRepository : MongoRepository<ProductPricing, Long> {
    @Query("{id:?0}")
    fun getProductPricingById(id: Long) : ProductPricing
    // fun findProductPricingById(id: Int) : ProductPricing
    // fun findOneById(id: Int)
}