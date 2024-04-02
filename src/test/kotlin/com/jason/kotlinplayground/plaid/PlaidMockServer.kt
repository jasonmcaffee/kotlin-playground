package com.jason.kotlinplayground.plaid
import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.io.File
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext

/**
 * Server that handles plaid requests
 */
class PlaidMockServer() {
    val mockWebServer = MockWebServer()

    init {
        val sslContext = SSLContext.getInstance("TLS")
        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())

        // Load the keystore
        val keyStore = KeyStore.getInstance("PKCS12")
        val keystoreInputStream = File("src/test/resources/mock_server_keys/mockserver.p12").inputStream() // Adjust the path
        keyStore.load(keystoreInputStream, "123456".toCharArray()) // Use your keystore password

        keyManagerFactory.init(keyStore, "123456".toCharArray()) // Use your key password
        sslContext.init(keyManagerFactory.keyManagers, null, null)

        // Set the SSL context to the MockWebServer
        mockWebServer.useHttps(sslContext.socketFactory, false)

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