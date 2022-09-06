package com.jason.kotlinplayground.kotlinbasics

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.*
import java.math.BigDecimal

class Basics {
    @Test fun `basic types`(){
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

    @Test fun `type inference`(){
        val str = "Tacos"
        val bool = true
        val char = 'C'
        val long = 6_000L
        assert(str is String)
        assert(bool is Boolean)
        assert(char is Char)
        assert(long is Long)
    }

    @Test fun `values and variables`(){
        //prefer immutable values (val) over mutable variables (var)
        val name = "Jason"
        //name = "Laura"  //"val cannot be reassigned" compilation error

        //only vars can be reassigned
        var first = "Jason"
        first = "Laura"
        assert(first == "Laura")
    }

    @Test fun `equality`(){
        val a = 1000
        val b = 1000
        assert(a == b) //would be false in java
        assert(a.equals(b))
    }

    @Test fun `declaring functions`(){
        fun sayHi(): String {
            return "hi"
        }
        //same thing, but with no explicit return type, no curly braces, no 'return' keyword
        fun sayHi2() = "hi"
        assert(sayHi() == sayHi2())
    }

    @Test fun `lists`(){
        val people = listOf("Jason", "Laura")
        assert(people[0] == "Jason")
    }

    @Test fun `sets`(){
        val uniquePeople = setOf("Jason", "Laura", "Jason", "Laura")
        assert(uniquePeople.size == 2)
        assert("Jason" in uniquePeople)
    }

    @Test fun `maps`(){
        val map = mapOf(
            "Mason" to "Bricks",
            "Carpenter" to "Wood"
        )
        assert("Mason" in map)
        assert(map["Mason"] == "Bricks")
        //map["Mason"] = "Arrows"  //immutable

        val mutableMap = mutableMapOf(
            "Mason" to "Bricks"
        )
        mutableMap["Carpenter"] = "Wood"
        //mutableMap.put("Carpenter", "Wood")  //non-preferred alternative
        assert(map["Carpenter"] == "Wood")
    }

    @Test fun `alternative collections`(){
        val treeMap = java.util.TreeMap(
            mapOf(
                "Mason" to "Bricks",
                "Carpenter" to "Wood"
            )
        )
        assert(treeMap.get("Mason") == "Bricks")
    }

    @Test fun `arrays`(){
        //only needed for varargs and main function. otherwise use lists
        val arr = arrayOf("Tacos", "Burritos")
        assert(arr[0] == "Tacos")
    }

    @Test fun `if statements`(){
        //if is an expression, meaning that it returns a value.
        val planet = if(true) "Jupiter" else "Uranus"
        assert(planet == "Jupiter")
    }

    @Test fun `when statements`(){
        val planet = if(true) "Earth" else "Mercury"
        //when is an expression, meaning that it returns a value
        val planetMassKg = when (planet){
            "Earth" -> BigDecimal("5.97219E24")
            "Jupiter" -> BigDecimal("1.89813E27")
            "Mercury" -> BigDecimal("3.285E23")
            else -> null //must be exhaustive
        }
        assert(planetMassKg == BigDecimal("5.97219E24"))
    }

    @Test fun `string interpolation`(){
        val elements = mapOf(
            "Hydrogen" to BigDecimal("1.6735575E-27"),
        )
        val hydrogen = "Hydrogen"
        val hydrogenDisplay = "The atomic mass of $hydrogen is ${elements["Hydrogen"]}"
        assert(hydrogenDisplay == "The atomic mass of Hydrogen is 1.6735575E-27")
    }

    @Test fun `multiline strings`(){
        val `Fire and Ice by Robert Frost` = """
            Some say the world will end in fire,
            Some say in ice.
            From what I’ve tasted of desire
            I hold with those who favour fire.
            But if it had to perish twice,
            I think I know enough of hate
            To say that for destruction ice
            Is also great
            And would suffice.
            "Fire and Ice" by Robert Frost
        """.trimIndent()
        val uglierFormatting =
                "Some say the world will end in fire,\n" +
                "Some say in ice.\n" +
                "From what I’ve tasted of desire\n" +
                "I hold with those who favour fire.\n" +
                "But if it had to perish twice,\n" +
                "I think I know enough of hate\n" +
                "To say that for destruction ice\n" +
                "Is also great\n" +
                "And would suffice.\n" +
                "\"Fire and Ice\" by Robert Frost" //have to escape quotes whereas that wasn't needed with multiline
        assert(`Fire and Ice by Robert Frost` === uglierFormatting)
    }

    @Test fun `for loop`(){
        val nobleGases = listOf("Helium", "Argon", "Krypton", "Xenon", "Neon", "Radon", "Oganesson",)
        for(gas in nobleGases){
            assert(nobleGases.contains(gas))
        }
        nobleGases.forEach {
            assert(nobleGases.contains(it))
        }
    }

}