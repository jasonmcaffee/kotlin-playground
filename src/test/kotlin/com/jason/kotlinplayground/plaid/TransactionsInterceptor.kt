package com.jason.kotlinplayground.plaid

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
class TransactionsInterceptor(val filePath: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val protocol = request.url.scheme
        // Check if the request URL matches the Plaid transactions endpoint
        if (url.contains("transactions/get")) {
            // Load the transactions.json file
            val jsonData = File(filePath).readText()
            return Response.Builder()
                .request(request)
//                .protocol(Protocol.get(protocol))
                .protocol(Protocol.HTTP_2)
                .code(200) // HTTP  200 OK
                .message("OK")
                .body(ResponseBody.create("application/json".toMediaTypeOrNull(), jsonData))
                .build()
        }

        // If the request doesn't match the endpoint, proceed with the original request
        return chain.proceed(request)
    }
}