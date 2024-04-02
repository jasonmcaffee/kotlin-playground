package com.jason.kotlinplayground.plaid

import com.plaid.client.ApiClient
import com.plaid.client.model.TransactionsGetRequest
import com.plaid.client.model.TransactionsGetResponse
import com.plaid.client.request.PlaidApi
import com.shadowed.plaid.client.PlaidApiService
import com.shadowed.plaid.client.PlaidClient
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach

import com.shadowed.plaid.client.PlaidClient as OldPlaidClient
import com.shadowed.plaid.client.PlaidApiService as OldPlaidApiService
import com.shadowed.plaid.client.request.TransactionsGetRequest as OldTransactionsGetRequest
import com.shadowed.plaid.client.response.TransactionsGetResponse as OldTransactionsGetResponse

import org.junit.jupiter.api.Test
import java.io.File
import java.security.cert.X509Certificate
import java.sql.Date
import java.time.LocalDate
import javax.net.ssl.*


class PlaidClientMockServerTests {
    private lateinit var plaidMockServer: PlaidMockServer

    @AfterEach
    fun teardown(){
        plaidMockServer?.shutdown()
    }


    @Test
    fun `uses plaid mock server to get transactions from a json file`(){
        val plaidService = createPlaidServiceThatUsesPlaidMockServer()
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

    @Test
    fun `OLD uses plaid mock server to get transactions from a json file`(){
        val plaidService = createOldPlaidService()
        val request = OldTransactionsGetRequest(
            accessToken, Date.valueOf(LocalDate.of(2023, 1, 1)), Date.valueOf(LocalDate.of(2024, 1, 1))
        )

        val transactionsResponse = callPlaid<OldTransactionsGetResponse> {
            plaidService?.transactionsGet(request)?.execute()!!
        }
        val transactions = transactionsResponse?.transactions

        assert(transactions?.size == 100)
    }

    fun createPlaidServiceThatUsesPlaidMockServer(): PlaidApi {
        val apiKeys = hashMapOf(
            "clientId" to "617975b96aaa7c0011b0a846",
            "secret" to "9cd3e5bd00e10c3a723f85c90d2f95",
        )

        plaidMockServer = PlaidMockServer()
        val url = plaidMockServer.getUrl()
        println("Plaid mock server running on url $url")

        val apiClient = ApiClient(apiKeys)
        apiClient.setPlaidAdapter(url)
        val plaidClient = apiClient.createService(PlaidApi::class.java)
        return plaidClient
    }

    fun createOldPlaidService(): OldPlaidApiService? {
        plaidMockServer = PlaidMockServer()
        val url = plaidMockServer.getUrl()

        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        val oldClient = OldPlaidClient.newBuilder()
            .clientIdAndSecret(apiKeys.get("clientId"), apiKeys.get("secret"))
            .baseUrl(url)

        oldClient.okHttpClientBuilder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier(object : HostnameVerifier {
                override fun verify(hostname: String?, session: SSLSession?): Boolean = true
            })

        oldClient.gsonBuilder().registerTypeAdapter(com.shadowed.plaid.client.response.TransactionsGetResponse.Transaction::class.java, TransactionDeserializer())

        val service = oldClient.build().service()
        return service
    }

}