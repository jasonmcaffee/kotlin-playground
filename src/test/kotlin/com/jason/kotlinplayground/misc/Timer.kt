package com.jason.kotlinplayground.misc

import org.junit.jupiter.api.Test
import org.springframework.util.StopWatch

inline fun timeMilli(func: (getMilli: ()-> Long) -> Unit){
    val stopWatch = StopWatch()
    stopWatch.start()

    func { //pass in a lambda that can be executed by the caller at the end of their code block
        stopWatch.stop()
        val result = stopWatch.totalTimeMillis //return the total ms
        stopWatch.start()
        result
    }
}

class TimerTests {

    @Test fun `should time`(){
        timeMilli { getMilli ->
            Thread.sleep(1000)
            var durationMs = getMilli()
            assert(durationMs >= 1000 && durationMs < 1100)

            Thread.sleep(1000)
            durationMs = getMilli()
            assert(durationMs >= 2000 && durationMs < 2100)
        }
    }
}