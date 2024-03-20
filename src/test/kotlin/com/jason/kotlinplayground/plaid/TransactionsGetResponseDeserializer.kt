package com.jason.kotlinplayground.plaid
import com.shadowed.plaid.client.response.TransactionsGetResponse as OldTransactionsGetResponse
import com.google.gson.*
import java.lang.reflect.Type

class TransactionsGetResponseDeserializer : JsonDeserializer<OldTransactionsGetResponse> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): OldTransactionsGetResponse {
        val jsonObject = json.asJsonObject

        // Deserialize the rest of TransactionsGetResponse normally
        val response = Gson().fromJson(jsonObject, OldTransactionsGetResponse::class.java)

        // Handle `personal_finance_category` manually
        val transactionsArray = jsonObject.getAsJsonArray("transactions")
        transactionsArray.forEach { transactionElement ->
            val personalFinanceCategory = transactionElement.asJsonObject.get("personal_finance_category")
            println("hijacked deserialize personal finance: " + personalFinanceCategory)
            // Do something with personalFinanceCategory, like adding it to a custom field in Transaction
        }

        return response
    }

}
