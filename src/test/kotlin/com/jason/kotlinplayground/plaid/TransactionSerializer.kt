//package com.jason.kotlinplayground.plaid
//
//import com.google.gson.JsonArray
//import com.google.gson.JsonElement
//import com.google.gson.JsonObject
//import com.google.gson.JsonSerializationContext
//import com.google.gson.JsonSerializer
//import com.shadowed.plaid.client.response.TransactionsGetResponse as OldTransactionsGetResponse
//import java.lang.reflect.Type
//
//class TransactionSerializer : JsonSerializer<OldTransactionsGetResponse.Transaction>{
//    override fun serialize(src: OldTransactionsGetResponse.Transaction?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
//        // Step 1: Serialize the entire src object
//        val srcSerialized = context?.serialize(src) ?: JsonObject()
//
//        // Check if srcSerialized is a JsonObject to safely operate on it
//        if (srcSerialized is JsonObject) {
//
//        }
//
//        return srcSerialized
//    }
//}