package com.jason.kotlinplayground.serialization

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders

enum class QuarkType{
    UP,
    DOWN,
    STRANGE,
    CHARM,
    BOTTOM,
    TOP
}
data class Quark (val type: QuarkType)

class GSONTest {


    @Test fun `unknown enum deserialization`() {
        val gson = Gson()

        val json1 = """
            {
                "type": "TOP"
            }
        """.trimIndent()

        val quark: Quark = gson.fromJson(json1, object : TypeToken<Quark>(){}.type)
        assert(quark.type == QuarkType.TOP)

        val json2 = """
            {
                "type": "NOT_YET_DISCOVERED"
            }
        """.trimIndent()

        val quark2: Quark = gson.fromJson(json2, object : TypeToken<Quark>(){}.type)
        assert(1 == 1)
    }
}