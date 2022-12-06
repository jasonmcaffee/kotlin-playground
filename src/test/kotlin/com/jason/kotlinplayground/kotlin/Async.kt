package com.jason.kotlinplayground.kotlin

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import kotlin.coroutines.CoroutineContext

suspend fun suspendFunc(){
    println("starting suspendFunc..")
    delay(10000L)
    println("completed suspendFunc...")
}

class Async {
    @Test fun `launch with suspend func`() = runBlocking{
        launch {
            suspendFunc()
        }
        println("didnt wait for launch")
        assert(1 == 1)
    }

    @Test fun `launch dont wait`(){
        fun something() {
            GlobalScope.launch{
                println("something start...")
                delay(10000L)
                println("something end...")
                assert(1==2)
            }
        }
        something()
        assert(1 == 1)

    }

    // @Test fun `async stuff`() = runBlocking{
    //     // async{
    //     //     println("hello")
    //     // }
    //     suspend fun what(){
    //         async{
    //
    //         }
    //     }
    //     async(Dispatchers.IO, CoroutineStart.DEFAULT){
    //
    //     }
    // }
}