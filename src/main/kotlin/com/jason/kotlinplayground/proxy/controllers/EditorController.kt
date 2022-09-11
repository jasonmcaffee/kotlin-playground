package com.jason.kotlinplayground.proxy.controllers

import com.jason.kotlinplayground.proxy.models.CachedResponse
import com.jason.kotlinplayground.proxy.services.EditorService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
const val JSON = MediaType.APPLICATION_JSON_VALUE
@RestController
@RequestMapping("/editor")
class EditorController(private val editorService: EditorService) {

    @GetMapping("/cachedResponses", produces = [JSON])
    suspend fun getCachedResponsesForUrl(@RequestParam url: String,): List<CachedResponse> {
        return editorService.getCachedResponsesForUrl(url)
    }
}