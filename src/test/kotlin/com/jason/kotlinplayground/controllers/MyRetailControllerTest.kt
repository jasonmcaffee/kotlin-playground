package com.jason.kotlinplayground.controllers

import com.jason.kotlinplayground.models.CurrencyCode
import com.jason.kotlinplayground.models.CurrentPrice
import com.jason.kotlinplayground.models.ProductPricing
import com.jason.kotlinplayground.repositories.ProductPricingRepository
import io.restassured.RestAssured.given
import io.restassured.filter.log.LogDetail
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

/**
 * Test by using embedded mongo and okhttp3 mock server for redsky api.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MyRetailControllerTest(
    @LocalServerPort private val port: Int,
    @Autowired private val productPricingRepository: ProductPricingRepository, //embedded mongo
) {
    //json for redsky mock server
    private val bigLebowskiRedSkyResponse13860428 = javaClass.getResource("/redskyresponses/big_lebowski.json")!!.readText()
    private val conanRedSkyResponse13860427 = javaClass.getResource("/redskyresponses/conan_the_barbarian.json")!!.readText()

    companion object{
        @DynamicPropertySource
        @JvmStatic
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("redskyclient.base-uri") { "http://localhost:${mockRedSkyAPI.port}" }
        }

        @JvmStatic
        val mockRedSkyAPI = MockWebServer()
    }

    @BeforeAll
    fun setUp() {}

    @AfterEach
    fun tearDown() {
        productPricingRepository.deleteAll()
    }

    @Test
    fun getProductByIdHappyPath(){
        val productId = 13860428L
        val currentPriceValue = 13.49
        val currencyCode = CurrencyCode.USD
        enqueueRedSkyResponseForBigLebowski()
        productPricingRepository.save(ProductPricing(productId, currentPrice = CurrentPrice(currentPriceValue, currencyCode)))
        given()
            .contentType("application/json")
            .accept("application/json")
            .expect()
            .statusCode(200)
            .body("id", equalTo(productId.toInt()))
            .body("name", equalTo("The Big Lebowski (Blu-ray)"))
            .body("current_price.value", equalTo(currentPriceValue.toFloat()))
            .body("current_price.currency_code", equalTo(currencyCode.toString()))
            .log().ifValidationFails(LogDetail.BODY)
            .`when`().get("http://localhost:$port/my-retail/products/$productId")
    }

    @Test
    fun getProductByIdFoundInRedSkyButNotInDatabase(){
        enqueueRedSkyResponseForBigLebowski()
        given()
            .contentType("application/json")
            .accept("application/json")
            .expect()
            .statusCode(404)
            .body("message", equalTo("no pricing found for product id: 13860428"))
            .log().ifValidationFails(LogDetail.BODY)
            .`when`().get("http://localhost:$port/my-retail/products/13860428")
    }

    @Test
    fun getProductByIdFoundInDatabaseButNotInRedSky(){
        val productId = 13860428L
        enqueueRedSkyResponseForNotFound()
        productPricingRepository.save(ProductPricing(productId, currentPrice = CurrentPrice(13.49, CurrencyCode.USD)))
        given()
            .contentType("application/json")
            .accept("application/json")
            .expect()
            .statusCode(404)
            .body("message", equalTo("product was not found"))
            .log().ifValidationFails(LogDetail.BODY)
            .`when`().get("http://localhost:$port/my-retail/products/$productId")
    }

    @Test
    fun updateProductPricingHappyPath(){
        val productId = 13860428L
        productPricingRepository.save(ProductPricing(productId, currentPrice = CurrentPrice(13.49, CurrencyCode.USD)))

        val newPrice = 22.22
        given()
            .contentType("application/json")
            .accept("application/json")
            .body(createUpdateProductRequestBody(newPrice, CurrencyCode.USD, "wont change"))
            .expect()
            .statusCode(200)
            .log().ifValidationFails(LogDetail.BODY)
            .log().body()
            .`when`().put("http://localhost:$port/my-retail/products/$productId")

        enqueueRedSkyResponseForBigLebowski()
        given()
            .contentType("application/json")
            .accept("application/json")
            .expect()
            .statusCode(200)
            .body("id", equalTo(productId.toInt()))
            .body("name", equalTo("The Big Lebowski (Blu-ray)"))
            .body("current_price.value", equalTo(newPrice.toFloat()))
            .body("current_price.currency_code", equalTo(CurrencyCode.USD.toString()))
            .log().ifValidationFails(LogDetail.BODY)
            .`when`().get("http://localhost:$port/my-retail/products/$productId")
    }

    @Test
    fun updateProductPricingThatIsNotFound(){
        given()
            .contentType("application/json")
            .accept("application/json")
            .body(createUpdateProductRequestBody(22.33, CurrencyCode.USD, "wont change"))
            .expect()
            .statusCode(404)
            .log().ifValidationFails(LogDetail.BODY)
            .`when`().put("http://localhost:$port/my-retail/products/13860428")
    }

    @Test
    fun updateProductPricingWithLessThanZeroValue(){
        val productId = 13860428L
        productPricingRepository.save(ProductPricing(productId, currentPrice = CurrentPrice(13.49, CurrencyCode.USD)))
        given()
            .contentType("application/json")
            .accept("application/json")
            .body(createUpdateProductRequestBody(-123.00, CurrencyCode.USD, "wont change"))
            .expect()
            .statusCode(400)
            .body("'current_price.value'", equalTo("Price must not be less than 0"))
            .log().ifValidationFails(LogDetail.BODY)
            .`when`().put("http://localhost:$port/my-retail/products/$productId")
    }



    fun enqueueRedSkyResponseForBigLebowski() = enqueueRedSkyResponse(bigLebowskiRedSkyResponse13860428)

    fun enqueueRedSkyResponse(jsonResponse: String){
        mockRedSkyAPI.enqueue(
            MockResponse()
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)
        )
    }

    fun enqueueRedSkyResponseForNotFound(){
        mockRedSkyAPI.enqueue(MockResponse()
            .setBody("{\"errors\":[{\"message\":\"No product found with tcin 1\"}]}")
            .setResponseCode(404))
    }

    fun createUpdateProductRequestBody(value: Double, currencyCode: CurrencyCode, productName: String): String{
        return """
           {
                 "name": "$productName",
                 "current_price": {
                    "value": $value,
                    "currency_code": "$currencyCode"
                 }
           }
        """
    }

}