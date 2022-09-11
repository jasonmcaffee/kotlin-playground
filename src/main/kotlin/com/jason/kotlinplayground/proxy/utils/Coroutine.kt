package com.jason.kotlinplayground.proxy.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * less code to type
 * rather than:
 * withContext(Dispatcher.IO) { ... }
 * you can use:
 * io { ... }
 */
suspend fun <T> io(
    block: suspend CoroutineScope.() -> T
) = withContext(Dispatchers.IO, block)