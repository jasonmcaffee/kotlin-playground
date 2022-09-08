package com.jason.kotlinplayground.redsky.redskyClient
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitObjectResult
import com.jason.kotlinplayground.redsky.redskyClient.deserializers.GsonDeserializer
import com.jason.kotlinplayground.redsky.redskyClient.models.ProductInfoResponse
import com.jason.kotlinplayground.redsky.redskyClient.models.RedSkyClientException
import com.jason.kotlinplayground.redsky.redskyClient.models.RedSkyClientNotFoundException
import kotlinx.coroutines.coroutineScope
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RedSkyClient {
    @Value("\${redskyclient.base-uri}")
    lateinit var redskyClientBaseUri: String


    suspend fun getProductInfo(id: Long): ProductInfoResponse = coroutineScope {

        val (productInfoResponse, error) = Fuel.get("$redskyClientBaseUri/redsky_aggregations/v1/redsky/case_study_v1?key=3yUxt7WltYG7MFKPp7uyELi1K40ad2ys&tcin=$id")
            .awaitObjectResult(GsonDeserializer())

        if(error != null){
            if(error.response.statusCode == 404){
                throw RedSkyClientNotFoundException(error.message ?: "Product id: $id was not found")
            }
            throw RedSkyClientException(error.message ?: "Error communicating with RedSky")
        }
        return@coroutineScope productInfoResponse!!
    }

}