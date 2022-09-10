package com.jason.kotlinplayground.proxy.repositories

import com.jason.kotlinplayground.proxy.models.CachedResponse
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

//https://kotlinlang.org/docs/jvm-spring-boot-restful.html#add-database-support
@Repository
interface CachedResponseRepository: CrudRepository<CachedResponse, Long> {
    @Query("select * from cached_response")
    fun findCachedResponses(): List<CachedResponse>
}