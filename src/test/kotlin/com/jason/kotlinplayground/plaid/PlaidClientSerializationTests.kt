package com.jason.kotlinplayground.plaid
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.plaid.client.ApiClient
import com.plaid.client.model.PlaidError
import com.plaid.client.model.TransactionsGetRequest
import com.plaid.client.model.TransactionsGetResponse

import com.plaid.client.request.PlaidApi
import org.junit.jupiter.api.Test
import okhttp3.*
import java.io.File
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

import com.shadowed.plaid.client.PlaidClient as OldPlaidClient
import com.shadowed.plaid.client.PlaidApiService as OldPlaidApiService
import com.shadowed.plaid.client.request.TransactionsGetRequest as OldTransactionsGetRequest
import com.shadowed.plaid.client.response.TransactionsGetResponse as OldTransactionsGetResponse

val apiKeys = hashMapOf(
    "clientId" to "617975b96aaa7c0011b0a846",
    "secret" to "9cd3e5bd00e10c3a723f85c90d2f95",
)
val accessToken = "access-sandbox-84aa81b0-9c66-44f3-9443-338d54b49538"
val baseUrl = "https://sandbox.plaid.com"

class PlaidClientSerializationTests {
    @Test
    fun `should get transactions from disk using an http interceptor`(){
        //common
        val startDate = LocalDate.of(2023, 1, 1)
        val endDate =  LocalDate.of(2024, 3,1)

        //new client
        val plaidService = createPlaidServiceThatUsesPlaidMockServer()
        val request = TransactionsGetRequest()
        request.startDate = startDate
        request.endDate = endDate
        request.accessToken = accessToken
        val transactionsResponse = callPlaid<TransactionsGetResponse> {
            plaidService.transactionsGet(request).execute()
        }
        val transactions = transactionsResponse?.transactions

        assert(transactions?.size == 100)
        val transaction1 = transactions?.get(0)!!
        println(transaction1.personalFinanceCategory?.primary)
        println(transaction1.personalFinanceCategory?.detailed)
        println(transaction1.category) //deprecated
//        assert(transaction1.transactionId == "el49jjdkrPcNW7kBoeKjtmPk3d9jBmhr9e54V")
//        println(transactionsResponse)

        //old client
        val oldPlaidApiService = createOldPlaidService()
        val oldRequest = OldTransactionsGetRequest(accessToken, localDateToDate(startDate), localDateToDate(endDate))
        val oldTransactionsResponse = callPlaid<OldTransactionsGetResponse>{
            oldPlaidApiService?.transactionsGet(oldRequest)?.execute()!!
        }
        val oldTransaction0 = oldTransactionsResponse?.transactions?.get(0)
        assert(oldTransaction0?.accountId != null)
        val oldTransactions = oldTransactionsResponse?.transactions
        val oldTransactionsWithPFC = oldTransactions?.mapNotNull { it as TransactionWithPFC }
        assert(oldTransactions?.size == 100)
//        val oldTransaction1 = oldTransactions?.get(0)!! as TransactionWithPFC
        val oldTransaction1 = oldTransactionsWithPFC?.get(0)!!
        println(oldTransaction1.category)
        println(oldTransaction1.personalFinanceCategory?.primary)
        println(oldTransaction1.personalFinanceCategory?.detailed)
        println(oldTransaction1.personalFinanceCategory?.confidenceLevel)

        val gson = createGsonWithTransactionsWithPersonalFinanceCategorySerializer()
//        val oldTransactionsJson = gson.toJson(oldTransactionsResponse)
        val oldTransactionsJson = Gson().toJson(oldTransactionsResponse)
        assert(oldTransactionsJson.indexOf("personal_finance_category") > 0)
        assert(oldTransactionsJson.indexOf("item") > 0)
        assert(oldTransactionsJson.indexOf("accounts") > 0)
        assert(oldTransactionsJson.indexOf("requestId") > 0)
        assert(oldTransactionsJson.indexOf("totalTransactions") > 0)

//        println(oldTransactionsResponse)
    }
}

fun createGsonWithTransactionsWithPersonalFinanceCategorySerializer(): Gson {
    val gsonBuilder = GsonBuilder()
//    gsonBuilder.registerTypeAdapter(OldTransactionsGetResponse::class.java, TransactionsGetResponseSerializer())
//    gsonBuilder.registerTypeAdapter(OldTransactionsGetResponse.Transaction::class.java, TransactionSerializer())
    val gson = gsonBuilder.create()
    return gson
}

fun localDateToDate(localDate: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): Date {
    return Date.from(localDate.atStartOfDay(zoneId).toInstant())
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

fun createPlaidServiceThatUsesPlaidMockServer(): PlaidApi {
    //use a transactions interceptor to read transactions from disk
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(TransactionsInterceptor("src/test/resources/testdata/transactions.json"))
        .build()
    val apiClient = ApiClient(apiKeys)
//    val apiClient = ApiClient(okHttpClient)
    apiClient.setPlaidAdapter(baseUrl)
    val plaidClient = apiClient.createService(PlaidApi::class.java)
    return plaidClient
}

fun createOldPlaidService(): OldPlaidApiService? {
    val file = File("src/test/resources/testdata/temp.json")

    val oldClient = OldPlaidClient.newBuilder()
        .clientIdAndSecret(apiKeys.get("clientId"), apiKeys.get("secret"))
        .baseUrl(baseUrl)

//    oldClient.okHttpClientBuilder().addInterceptor(JsonResponseInterceptor(file)).build()
    oldClient.gsonBuilder().registerTypeAdapter(OldTransactionsGetResponse.Transaction::class.java, TransactionDeserializer())

    val service = oldClient.build().service()
    return service
}

//    oldClient.gsonBuilder().registerTypeAdapter(OldTransactionsGetResponse::class.java, TransactionsGetResponseDeserializer()).create()