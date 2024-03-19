package com.jason.kotlinplayground.plaid

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.sink
import okio.buffer
import java.io.File

class JsonResponseInterceptor(private val file: File) : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val responseBody = response.body
        val responseString = responseBody?.string()
        file.sink().buffer().use { sink ->
            responseString?.let {sink.writeUtf8(it)}
        }
        return response.newBuilder().body(responseString?.toResponseBody(responseBody?.contentType())).build()
    }
}