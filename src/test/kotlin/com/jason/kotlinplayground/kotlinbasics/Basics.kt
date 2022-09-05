package com.jason.kotlinplayground.kotlinbasics

import org.junit.jupiter.api.Test

class Basics {
    @Test
    fun `basic types`(){
        //numbers
        val int: Int = 1
        val long: Long = 6_000_000L
        val double: Double = 3_000.145
        //strings
        val string: String = "Jason"
        //bool
        val bool: Boolean = true
        //char
        val char: Char = 'C' //double quotes not allowed
    }

    @Test
    fun `type inference`(){

    }

    @Test
    fun `prefer values over variables`(){

        //prefer immutable values (val) over mutable variables (var)
        val name = "Jason"

        //only vars can be reassigned
        var first = "Jason"
        first = "Laura"
    }

}