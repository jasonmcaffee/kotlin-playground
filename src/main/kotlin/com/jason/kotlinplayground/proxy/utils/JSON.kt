package com.jason.kotlinplayground.proxy.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.postgresql.util.PGobject
import org.springframework.http.HttpHeaders

//https://www.bezkoder.com/kotlin-parse-json-gson/
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

fun httpHeadersFromJson(json: String?): HttpHeaders{
    if(json == null) {
        return HttpHeaders()
    }
    val gson = Gson()
    val httpHeaders: HttpHeaders = gson.fromJson(json, object : TypeToken<HttpHeaders>(){}.type)
    return httpHeaders
}

fun <T> fromJson(json: String?): T? {
    if(json == null){
        return null
    }
    val gson = Gson()
    val t : T = gson.fromJson(json, object: TypeToken<T>(){}.type)
    return t
}

fun httpHeadersFromPGobject(pGobject: PGobject?): HttpHeaders{
    return httpHeadersFromJson(pGobject?.value)
}

fun <T> fromPGobject(pgObject: PGobject?): T?{
    return fromJson<T>(pgObject?.value)
}