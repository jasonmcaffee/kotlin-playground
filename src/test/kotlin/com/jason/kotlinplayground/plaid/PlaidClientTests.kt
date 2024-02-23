package com.jason.kotlinplayground.plaid
import com.google.gson.Gson
import com.plaid.client.ApiClient
import com.plaid.client.model.PlaidError
import com.plaid.client.model.TransactionsGetRequest
import com.plaid.client.model.TransactionsGetRequestOptions
import com.plaid.client.model.TransactionsGetResponse
import com.plaid.client.request.PlaidApi
import org.junit.jupiter.api.Test
import okhttp3.*
import java.time.LocalDate

val apiKeys = hashMapOf(
    "clientId" to "617975b96aaa7c0011b0a846",
    "secret" to "9cd3e5bd00e10c3a723f85c90d2f95",
)
val accessToken = "access-sandbox-84aa81b0-9c66-44f3-9443-338d54b49538"
val baseUrl = "https://sandbox.plaid.com"

class PlaidClientTests {
    @Test
    fun `should get transactions from disk using an http interceptor`(){
        println("hi")
        val plaidClient = createClient()
        val request = TransactionsGetRequest()
        request.startDate = LocalDate.of(2023, 1, 1)
        request.endDate = LocalDate.of(2024, 3,1)
        request.accessToken = accessToken
        val transactionsResponse = callPlaid<TransactionsGetResponse> {
            plaidClient.transactionsGet(request).execute()
        }
        val transactions = transactionsResponse?.transactions

        assert(transactions?.size == 100)
        val transaction1 = transactions?.get(0)!!
        assert(transaction1.transactionId == "el49jjdkrPcNW7kBoeKjtmPk3d9jBmhr9e54V")
        println(transactionsResponse)
    }
}

fun <TResponse>callPlaid(func: () -> retrofit2.Response<TResponse> ): TResponse?{
    val response = func()
    if(!response.isSuccessful){
        val error = decodePlaidError(response)
        throw PlaidException(error)
    }
    val actualResponse = response.body()
    return actualResponse
}

class PlaidException(val plaidError: PlaidError) : Exception() {
    override val message: String
        get() = "Plaid Error ${plaidError.errorMessage}"
}

fun decodePlaidError(response: retrofit2.Response<*>): PlaidError{
    try{
        val gson = Gson()
        val error = gson.fromJson(response.errorBody()?.string(), PlaidError::class.java)
        return error
    } catch (e: Exception){
        throw Exception("failed converting error: $e")
    }
}

fun createClient(): PlaidApi {
    //use a transactions interceptor to read transactions from disk
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(TransactionsInterceptor("src/test/resources/testdata/transactions.json"))
        .build()
//    val apiClient = ApiClient(apiKeys)
    val apiClient = ApiClient(okHttpClient)
    apiClient.setPlaidAdapter(baseUrl)
    val plaidClient = apiClient.createService(PlaidApi::class.java)
    return plaidClient
}