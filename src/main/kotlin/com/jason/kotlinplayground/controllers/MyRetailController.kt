package com.jason.kotlinplayground.controllers

import com.jason.kotlinplayground.models.response.ProductResponse
import com.jason.kotlinplayground.services.MyRetailService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/my-retail")
class MyRetailController(
    private val myRetailService: MyRetailService
) {
    @GetMapping("/products/{id}", produces=[MediaType.APPLICATION_JSON_VALUE])
    suspend fun getProductById(@PathVariable id: Long) : ProductResponse{
        return myRetailService.getProductById(id)
    }

}

