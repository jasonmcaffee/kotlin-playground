package com.jason.kotlinplayground.plaid
import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.io.File

/**
 * Server that handles plaid requests
 */
class PlaidMockServer() {
    val mockWebServer = MockWebServer()

    init {
        mockWebServer.dispatcher = object: Dispatcher(){
            override fun dispatch(request: RecordedRequest): MockResponse {
                return handleRequest(request)
            }
        }
    }

    fun shutdown(){
        mockWebServer.shutdown()
    }

    fun getPort(): Int {
        return mockWebServer.port
    }

    fun getUrl(): String {
        return mockWebServer.url("/").toString()
    }

    fun handleRequest(request: RecordedRequest): MockResponse {
        val requestUrl = request.path
        return when (requestUrl) {
            "/transactions/get" -> handleTransactionsGet(request)
            else -> throw NotImplementedError("Plaid mock server does not support url $requestUrl yet")
        }
    }

    fun handleTransactionsGet(request: RecordedRequest): MockResponse {
        val transactionsJsonFile = File("src/test/resources/testdata/transactions.json")
        val json = transactionsJsonFile.readText()
        val response = MockResponse().setBody(json)
        return response
    }
}