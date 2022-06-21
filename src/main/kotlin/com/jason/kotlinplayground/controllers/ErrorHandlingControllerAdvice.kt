package com.jason.kotlinplayground.controllers

import com.jason.kotlinplayground.models.exceptions.ProductPricingNotFoundException
import com.jason.kotlinplayground.models.response.ErrorResponse
import com.jason.kotlinplayground.redskyClient.models.RedSkyClientException
import com.jason.kotlinplayground.redskyClient.models.RedSkyClientNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.http.HttpStatus.BAD_GATEWAY
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ErrorHandlingControllerAdvice: ResponseEntityExceptionHandler() { //
    @ExceptionHandler(RedSkyClientNotFoundException::class)
    fun productNotFoundExceptionHandler(exception: RedSkyClientNotFoundException): ResponseEntity<ErrorResponse>{
        return ResponseEntity.status(NOT_FOUND).body(ErrorResponse("product was not found"))
    }

    @ExceptionHandler(RedSkyClientException::class)
    fun redSkyExceptionHandler(exception: RedSkyClientException): ResponseEntity<ErrorResponse>{
        return ResponseEntity.status(BAD_GATEWAY).body(ErrorResponse(exception.message!!))
    }

    @ExceptionHandler(ProductPricingNotFoundException::class)
    fun productPricingNotFoundHandler(exception: ProductPricingNotFoundException): ResponseEntity<ErrorResponse>{
        return ResponseEntity.status(NOT_FOUND).body(ErrorResponse(exception.message!!))
    }

    @ExceptionHandler(Exception::class)
    fun internalServerErrorHandler(exception:Exception): ResponseEntity<ErrorResponse>{
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(ErrorResponse("internal server error"))
    }


}