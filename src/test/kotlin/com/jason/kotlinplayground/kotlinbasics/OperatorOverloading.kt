package com.jason.kotlinplayground.kotlinbasics

import org.junit.jupiter.api.Test

class OperatorOverloading {
    @Test fun `in`(){
        class Taco {
            // `in` keyword is shorthand for contains.
            operator fun contains(value: CharSequence): Boolean {
                return true
            }
        }
        val t = Taco()
        var inCount = 0;
        when("tac"){
            in t -> ++inCount
        }
        assert(inCount == 1)
    }

    @Test fun `infix`(){
        data class Person(val first: String, val last: String)
        val people = listOf(Person("Mathew", "Smith"), Person("Mark", "Cuban"), Person("Luke", "Skywalker"))

        class From {
            lateinit var source: List<Person>
        }

        class Select {
            lateinit var from: From
            lateinit var propertyName: String
            fun execute(): List<String>{
                return when(this.propertyName){
                    "first" -> people.map { it.first }
                    "last" -> people.map {it. last}
                    else -> listOf()
                }
            }
        }

        fun select(propertyName: String, init: Select.() -> Unit): Select{
            val s = Select()
            s.propertyName = propertyName
            s.init()
            return s
        }

        infix fun Select.from(source: List<Person>): List<String>{
            this.from = From()
            this.from.source = source
            return this.execute()
        }

        val firstNames = select("first") {} from people

        assert(firstNames.size == 3)
        assert(firstNames.contains("Mathew"))
        assert(firstNames.contains("Mark"))
        assert(firstNames.contains("Luke"))
    }
}