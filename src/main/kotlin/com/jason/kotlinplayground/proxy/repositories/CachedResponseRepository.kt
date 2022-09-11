package com.jason.kotlinplayground.proxy.repositories

import com.jason.kotlinplayground.proxy.models.CachedResponse
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

//https://kotlinlang.org/docs/jvm-spring-boot-restful.html#add-database-support
@Repository
interface CachedResponseRepository: CrudRepository<CachedResponse, Long> {
    @Query("""
        select * from cached_response cr 
        where cr.url = :url 
        and (cr.request_body = :request_body or cr.request_body is null)
        and cr.request_method = :request_method
        order by cr.id desc
        limit 1
    """)
    fun findCachedResponseBy(@Param("request_method") requestMethod: String, @Param("url") url: String, @Param("request_body") requestBody: String?) : CachedResponse?

    @Query("""
        select * from cached_response cr
        where cr.url = :url
    """)
    fun findCachedResponseBy(@Param("url") url: String): List<CachedResponse>
}