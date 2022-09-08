package com.jason.kotlinplayground.redsky.controllers

import com.jason.kotlinplayground.redsky.request.UpdateProductRequest
import com.jason.kotlinplayground.redsky.models.response.ProductResponse
import com.jason.kotlinplayground.redsky.services.MyRetailService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/my-retail")
class MyRetailController(
    private val myRetailService: MyRetailService
) {
    @GetMapping("/products/{id}", produces=[MediaType.APPLICATION_JSON_VALUE])
    suspend fun getProductById(@PathVariable id: Long) : ProductResponse {
        return myRetailService.getProductById(id)
    }

    @PutMapping("/products/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces=[MediaType.APPLICATION_JSON_VALUE])
    fun updateProductPrice(@PathVariable id: Long, @Valid @RequestBody updateProductRequest: UpdateProductRequest){
         myRetailService.updateProductPrice(id, updateProductRequest)
    }

}

