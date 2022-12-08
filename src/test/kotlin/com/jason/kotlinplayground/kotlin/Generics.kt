package com.jason.kotlinplayground.kotlin

import org.junit.jupiter.api.Test

class Response<T>{
    val body: T
    constructor(b: T){
        body = b
    }
}

class ItemGet{
    val data: String
    constructor(d: String){
        data = d
    }
}

class SomethingElse{
    val someStuff: Int
    constructor(s: Int){
        someStuff = s
    }
}

class Generics {
    @Test
    fun `cast unknown generic`(){

        fun handleResponse(response: Response<*>){
            Generic<ItemGet>().checkType(response.body)
            // Generic<Response<ItemGet>>().checkType(response)
        }
        val r1 = Response(ItemGet("hello"))
        val r2 = Response(SomethingElse(123))

        handleResponse(r1)
        handleResponse(r2)
    }
}

inline fun <reified T> isItemGet(): Boolean{
    return when (T::class){
        ItemGet::class -> true
        else -> false
    }
}

class Generic<T : Any>(val klass: Class<T>) {
    companion object {
        inline operator fun <reified T : Any>invoke() = Generic(T::class.java)
    }

    fun checkType(t: Any?): Boolean {
        return klass.isAssignableFrom(t?.javaClass)
    }
}