package com.jason.kotlinplayground.proxy.services

import com.jason.kotlinplayground.proxy.models.CachedResponse
import com.jason.kotlinplayground.proxy.repositories.CachedResponseRepository
import com.jason.kotlinplayground.proxy.utils.io
import org.springframework.stereotype.Service

@Service
class EditorService(private val cachedResponseRepository: CachedResponseRepository) {
    suspend fun getCachedResponsesForUrl(url: String): List<CachedResponse>{
        val dbCachedResponses = io { cachedResponseRepository.findCachedResponseBy(url) }
        return dbCachedResponses
    }
}