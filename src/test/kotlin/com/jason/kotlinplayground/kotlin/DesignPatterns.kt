package com.jason.kotlinplayground.kotlin

// import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class DesignPatterns {

    //singletons should be lazy (ie only instantiated when needed for the first time)
    //singletons should be thread safe
    //singletons should be performant.
    //https://www.baeldung.com/kotlin/lazy-initialization
    @Test fun `singleton with lazy initialization`(){

        val numberOfInitCalls = AtomicInteger()
        data class SlowToInit(val data: String){
            init {
                numberOfInitCalls.incrementAndGet()
                Thread.sleep(1000)
            }
        }
        //lazy (Lazy<T>) doesn't get evaluated until it is first referenced.
        //you can use lazy {...} if you want access to the Lazy delegate, which has isInitialized property.
        val lazyValue by lazy {  SlowToInit("some data") }
        val threadCount = 2
        val countDownLatch = CountDownLatch(threadCount)
        val executorService = Executors.newFixedThreadPool(threadCount)
        executorService.submit{ println(lazyValue); countDownLatch.countDown() }
        executorService.submit{ println(lazyValue); countDownLatch.countDown() }
        executorService.awaitTermination(5, TimeUnit.SECONDS)
        countDownLatch.await()
        assert(numberOfInitCalls.get() == 1)
    }

    enum class ParticleType {
        PHOTON, PROTON, NEUTRON, ELECTRON, PION
    }
    @Test fun `factory`(){
        //http://www1.udel.edu/mvb/PS146htm/146nopp.html
        //http://www1.udel.edu/mvb/PS146htm/146ptabc.html
        open class Particle(val mass: Double, val charge: Int)
        class Photon(mass: Double, charge: Int): Particle(mass, charge)
        class Proton(mass: Double, charge: Int): Particle(mass, charge)
        class Neutron(mass: Double, charge: Int): Particle(mass, charge)
        class Electron(mass: Double, charge: Int): Particle(mass, charge)
        class Pion(mass: Double, charge: Int): Particle(mass, charge)

        fun createParticle(particleType: ParticleType, mass: Double, charge: Int): Particle{
            return when(particleType){
                ParticleType.PHOTON -> Photon(mass, charge)
                ParticleType.PROTON -> Proton(mass, charge)
                ParticleType.NEUTRON -> Neutron(mass, charge)
                ParticleType.ELECTRON -> Electron(mass, charge)
                ParticleType.PION -> Pion(mass, charge)
            }
        }

        val particle = createParticle(ParticleType.PROTON, 1.0, 0)
        assert(particle is Proton)
    }

    /**
     * Static factories provide several benefits:
     * - explicity name different object constructors
     * - exceptions are typically not expected from a constructor, but are from other methods
     * - constructors should be fast
     * - potentially cache results. ie. don't recreate objects for the same value.
     * - subclassing - less restrictive as we don't have to instantiate the constructors?
     */
    open class Person(val firstName: String, val lastName: String){
        companion object StaticFactory{
            fun fromString(fullName: String): Person{
                val (firstName, lastName) = fullName.split(" ")
                return Person(firstName, lastName)
            }
        }
    }
    @Test fun `static factory`(){
        val person = Person.fromString("Jason McAffee")
        assert(person.firstName == "Jason")
        assert(person.lastName == "McAffee")
    }

}