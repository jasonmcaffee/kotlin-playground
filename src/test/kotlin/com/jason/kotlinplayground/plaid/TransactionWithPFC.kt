package com.jason.kotlinplayground.plaid
import com.shadowed.plaid.client.response.TransactionsGetResponse.Transaction as OldTransaction
import com.google.gson.annotations.SerializedName
class TransactionWithPFC(): OldTransaction() {
    @SerializedName("personal_finance_category")
    var personalFinanceCategory: PersonalFinanceCategory? = null

    class PersonalFinanceCategory {
        var confidenceLevel: String? = null
        var detailed: String? = null
        var primary: String? = null
    }
}
