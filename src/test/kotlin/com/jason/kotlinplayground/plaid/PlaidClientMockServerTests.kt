package com.jason.kotlinplayground.plaid

import com.plaid.client.ApiClient
import com.plaid.client.model.TransactionsGetRequest
import com.plaid.client.model.TransactionsGetResponse
import com.plaid.client.request.PlaidApi
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Test
import java.time.LocalDate


class PlaidClientMockServerTests {
    private lateinit var plaidMockServer: PlaidMockServer

    @Test
    fun `uses plaid mock server to get transactions from a json file`(){
        val plaidService = createPlaidService()
        val request = TransactionsGetRequest()
        request.startDate = LocalDate.of(2023, 1, 1)
        request.endDate = LocalDate.of(2024, 3,1)
        request.accessToken = accessToken
        val transactionsResponse = callPlaid<TransactionsGetResponse> {
            plaidService.transactionsGet(request).execute()
        }
        val transactions = transactionsResponse?.transactions

        assert(transactions?.size == 100)
    }

    fun createPlaidService(): PlaidApi {
        val apiKeys = hashMapOf(
            "clientId" to "617975b96aaa7c0011b0a846",
            "secret" to "9cd3e5bd00e10c3a723f85c90d2f95",
        )
        val accessToken = "access-sandbox-84aa81b0-9c66-44f3-9443-338d54b49538"

        plaidMockServer = PlaidMockServer()
        val url = plaidMockServer.getUrl()
        println("Plaid mock server running on url $url")

        //use a transactions interceptor to read transactions from disk
//        val okHttpClient = OkHttpClient.Builder()
//            .build()
        val apiClient = ApiClient(apiKeys)
//    val apiClient = ApiClient(okHttpClient)
        apiClient.setPlaidAdapter(url)
        val plaidClient = apiClient.createService(PlaidApi::class.java)
        return plaidClient
    }
}