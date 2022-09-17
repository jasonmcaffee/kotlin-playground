package com.jason.kotlinplayground.kotlinbasics

import org.junit.jupiter.api.Test
import kotlin.reflect.full.memberProperties

class OperatorOverloading {
    @Test fun `in`(){
        class Taco {
            // `in` keyword is infix/shorthand for contains.
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


    @Test fun `LINQ api v2`(){
        data class Person(val first: String, val last: String)
        val people = listOf(Person("Mathew", "Smith"), Person("Mark", "Cuban"), Person("Luke", "Skywalker"))

        class From {
            lateinit var source: List<Any>
        }

        class Select {
            lateinit var from: From
            lateinit var propertyNames: List<String>

            fun execute(): List<Map<String, Any?>>{
                val result = mutableListOf<Map<String, Any?>>()

                val source = this.from.source
                source.forEach{ item ->
                    val itemResult = mutableMapOf<String, Any?>()
                    propertyNames.forEach{ selectPropertyName ->
                        item::class.memberProperties.forEach{
                            if(it.name == selectPropertyName){
                                val value = it.getter.call(item)
                                itemResult[selectPropertyName] = value
                            }
                        }
                    }
                    if(itemResult.isNotEmpty()){
                        result.add(itemResult)
                    }
                }

                return result
            }
        }

        fun select(vararg propertyNames: String): Select{
            val s = Select()
            s.propertyNames = propertyNames.asList()
            return s
        }

        infix fun Select.from(source: List<Any>): List<Map<String, Any?>>{
            this.from = From()
            this.from.source = source
            return this.execute()
        }

        //run the select query
        val results = select("first", "last") from people

        //check the results
        assert(results.size == 3)
        val (result1, result2, result3) = results
        assert(result1["first"] == "Mathew")
        assert(result1["last"] == "Smith")

        assert(result2["first"] == "Mark")
        assert(result2["last"] == "Cuban")

        assert(result3["first"] == "Luke")
        assert(result3["last"] == "Skywalker")
    }
}