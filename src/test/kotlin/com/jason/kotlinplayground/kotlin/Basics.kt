package com.jason.kotlinplayground.kotlin

import org.junit.jupiter.api.Test
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
        val double = 42.0
        assert(str is String)
        assert(bool is Boolean)
        assert(char is Char)
        assert(long is Long)
        assert(double is Double)
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

    @Test fun `loops`(){
        val nobleGases = listOf("Helium", "Argon", "Krypton", "Xenon", "Neon", "Radon", "Oganesson",)
        for(gas in nobleGases){
            assert(nobleGases.contains(gas))
        }
        nobleGases.forEach {
            assert(nobleGases.contains(it))
        }

        for(i in 1..3){
            println(i) //prints 1, 2, 3
        }
        //until and downTo are functions, of a feature called 'infix call'
        for (i in 1 until 3){
            println(i) //prints 1, 2
        }
        for(i in 3 downTo 1){
            println(i)//prints 3, 2, 1
        }

        var i = 0
        while(i < 2){
            println(i++) //prints 0, 1
        }
        i = 0
        do{
            println(i++) //prints 0, 1
        }while(i < 2)
    }

    @Test fun `classes`(){
        class Person(val name: String, address: String? = null){ //primary constructor with val property, which autogens getters/setters
            //in order for address to have getters/setters, it must be defined here.
            var address: String? = address
                set(value){
                    field = "set to: $value"
                }
                get() = field + " + get"
        }
        val person = Person("Jason")
        assert(person.name === "Jason")
        // person.name = "is immutable so won't work"
        person.address = "is mutable"
        assert(person.address == "set to: is mutable + get")
    }

    interface Meditator{
        fun obtainEnlightenment(): Unit
        val robeColor: String
        //default implementation
        fun getReasonForMeditating() = "Nirvana"
    }
    @Test fun `interfaces`(){
        class SinglePointMeditator(override val robeColor: String) : Meditator {
            override fun obtainEnlightenment() {
                TODO("Not yet implemented")
            }
        }

        val singlePointMeditator = SinglePointMeditator("orange")
        val errorCount = try{
            singlePointMeditator.obtainEnlightenment()
        }catch(e: NotImplementedError){
            1
        }
        assert(errorCount == 1)

        assert(singlePointMeditator.getReasonForMeditating() == "Nirvana")
    }

    @Test fun `abstract classes and visibility modifiers`(){
        abstract class FundamentalForce(val rangeInMeters: BigDecimal){
            fun getParticlesWithinRange(allParticles: List<String>) = allParticles.filter { isParticleWithinRange(it) }
            private fun isParticleWithinRange(particle: String) = true
            protected fun doPhysics() = "beep boop beep"
        }

        var initCallCount = 0
        class WeakNuclearForce(rangeInMeters: BigDecimal): FundamentalForce(rangeInMeters){
            init{
                this.doPhysics() //we can access protected functions
                // this.isParticleWithinRange() // we _cannot_ access private functions
                ++initCallCount
            }
        }

        val weakNuclearForce = WeakNuclearForce(BigDecimal("10E-17"))
        assert(initCallCount == 1)
        assert(weakNuclearForce.rangeInMeters == BigDecimal("10E-17"))
        assert(weakNuclearForce.getParticlesWithinRange(listOf("W", "Z")) == listOf("W", "Z"))
    }

    interface Author{
        val firstName: String
        val lastName: String
    }
    @Test fun `objects`(){
        var initCount = 0;
        //note: objects differ from classes in that they cannot have constructors, however, they can have an init block
        //anonymous object which implements the Author interface
        val anonymousObject = object : Author {
            init { initCount++ }
            override val firstName = "Ernest"
            override val lastName = "Hemingway"
            fun getFullName(): String{
                return "$firstName $lastName"
            }
        }

        assert(anonymousObject.getFullName() == "Ernest Hemingway")
        assert(initCount == 1)
    }

    @Test fun `data classes`(){
        data class BigO(val n: Int, val runtimeGrowth: (n: Int) -> Int){
            fun characterize() = when (runtimeGrowth(n)){
                1 -> "constant"
                n -> "linear"
                (n * n) -> "quadratic"
                else -> "unknown"
            }
        }
        //call suffix - when the last param is a function, it can go in {} instead of ()
        val constantGrowth = BigO(10) { n -> 1 }
        assert(constantGrowth.characterize() == "constant")

        //data classes get hashcode() and equals() for free.
        data class X(val y: String)
        val x1 = X("howdy")
        val x2 = X("howdy")
        assert(x1.hashCode() == x2.hashCode())
        assert(x1 == x2)

        //copy function
        val constantGrowth2 = constantGrowth.copy()
        assert(constantGrowth == constantGrowth2)
    }

    @Test fun `extension functions`(){
        //String is the "method receiver".  "this" refers to String
        fun String.mask() = "*".repeat(this.length)
        assert("hello".mask() == "*****")
    }

    @Test fun `destructuring`(){
        //destructure a list
        val items = listOf(11, 22, 33)
        val (item1, item2, item3) = items
        assert(item1 == 11)
        assert(item2 == 22)
        assert(item3 == 33)

        data class Person(val firstName: String, val lastName: String)
        val p = Person("Jason", "McAffee")
        val (firstName, lastName) = p
        assert(firstName == "Jason")
        assert(lastName == "McAffee")
        //destructure an object
        // val person = object: Author {
        //     override val firstName = "Jason"
        //     override val lastName = "McAffee"
        // }
        //Fails because must have component1() function.
        //val (firstName, lastName) = (person as Author)
    }
}