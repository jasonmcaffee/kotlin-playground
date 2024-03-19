package com.jason.kotlinplayground.plaid
import com.shadowed.plaid.client.response.TransactionsGetResponse.Transaction as OldTransaction
import com.google.gson.*
import java.lang.reflect.Type

class TransactionDeserializer : JsonDeserializer<OldTransaction> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TransactionWithPFC {
        val jsonObject = json.asJsonObject
        val response = Gson().fromJson(jsonObject, TransactionWithPFC::class.java)
        return response
    }
}

//        val pfc = jsonObject.getAsJsonObject("personal_finance_category")
//        println("personal_fincance_category" + pfc)