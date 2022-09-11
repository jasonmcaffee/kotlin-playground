package com.jason.kotlinplayground.proxy.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.postgresql.util.PGobject

fun toJson(obj: Any) : String{
    val gsonPretty  = GsonBuilder().setPrettyPrinting().create()
    val jsonObj = gsonPretty.toJson(obj)
    return jsonObj
}

fun toPGObject(obj: Any): PGobject{
    val json = toJson(obj)
    val pGobject = PGobject()
    pGobject.type = "json"
    pGobject.value = json
    return pGobject
}