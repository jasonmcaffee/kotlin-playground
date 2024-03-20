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
//class TransactionsGetResponseSerializer: JsonSerializer<OldTransactionsGetResponse> {
//
//    override fun serialize(src: OldTransactionsGetResponse?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
//        // Step 1: Serialize the entire src object
//        val srcSerialized = context?.serialize(src) ?: JsonObject()
//
//        // Check if srcSerialized is a JsonObject to safely operate on it
//        if (srcSerialized is JsonObject) {
//            val jsonArray = JsonArray()
//
//            // Step 3: Serialize transactions and add them to jsonArray
//            src?.transactions?.forEach { transaction ->
//                when (transaction) {
//                    is TransactionWithPFC -> jsonArray.add(context?.serialize(transaction, TransactionWithPFC::class.java))
//                    else -> jsonArray.add(context?.serialize(transaction, OldTransactionsGetResponse.Transaction::class.java))
//                }
//            }
//
//            // Step 4: Replace the transactions field in srcSerialized
//            srcSerialized.add("transactions", jsonArray)
//        }
//
//        return srcSerialized
//    }
//}