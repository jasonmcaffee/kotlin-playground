package com.jason.kotlinplayground.plaid

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.shadowed.plaid.client.response.TransactionsGetResponse as OldTransactionsGetResponse
import java.lang.reflect.Type

class TransactionsGetResponseSerializer: JsonSerializer<OldTransactionsGetResponse> {

    override fun serialize(src: OldTransactionsGetResponse?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val jsonObject = JsonObject()
        val jsonArray = JsonArray()

        src?.transactions?.forEach { transaction ->
            when (transaction) {
                is TransactionWithPFC -> jsonArray.add(context?.serialize(transaction, TransactionWithPFC::class.java))
                else -> jsonArray.add(context?.serialize(transaction, OldTransactionsGetResponse.Transaction::class.java))
            }
        }

        jsonObject.add("transactions", jsonArray)
        return jsonObject
    }
}