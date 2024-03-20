package com.jason.kotlinplayground.plaid
import com.shadowed.plaid.client.response.TransactionsGetResponse.Transaction as OldTransaction
import com.google.gson.annotations.SerializedName
class TransactionWithPFC(): OldTransaction() {
    @SerializedName("personal_finance_category")
    var personalFinanceCategory: PersonalFinanceCategory? = null

    class PersonalFinanceCategory {
        @SerializedName("confidence_level")
        var confidenceLevel: String? = null
        @SerializedName("detailed")
        var detailed: String? = null
        @SerializedName("primary")
        var primary: String? = null
    }
}
