package com.jason.kotlinplayground.kotlin

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class CircuitBreakerTrippedException(message: String): Exception(message)

class CircuitBreaker(
    val tripAfterNConsecutiveExceptions: Int,
    val shouldRethrowExceptions: Boolean = false
) {
    var consecutiveExceptionCount: Int = 0

    fun process(func: () -> Unit){
        try{
            func()
            consecutiveExceptionCount = 0
        }catch(e: Exception){
            if(++consecutiveExceptionCount >= tripAfterNConsecutiveExceptions){
                throw CircuitBreakerTrippedException("Circuit breaker encountered $consecutiveExceptionCount exceptions, exceeding max of $tripAfterNConsecutiveExceptions")
            }
            if(shouldRethrowExceptions){ throw e }
        }
    }
}


class CircuitBreakerTests {
    @Test
    fun `should stop after N errors`() = runBlocking{
        var circuitBreakerTrippedCount = 0
        try{
            val circuitBreaker = CircuitBreaker(1)
            circuitBreaker.process {
                throw Exception("immediate failure")
            }
        }catch(e: CircuitBreakerTrippedException){
            circuitBreakerTrippedCount++
        }

        assert(circuitBreakerTrippedCount == 1)
    }

    @Test
    fun `should continue processing with intermittent errors`() = runBlocking{
        var circuitBreakerTrippedCount = 0
        try{
            val circuitBreaker = CircuitBreaker(2)
            circuitBreaker.process {
                for (i in 1..10){
                    if(i%2 == 0){ throw Exception("intermittent failure") }
                }
            }
        }catch(e: CircuitBreakerTrippedException){
            circuitBreakerTrippedCount++
        }

        assert(circuitBreakerTrippedCount == 0)
    }
}