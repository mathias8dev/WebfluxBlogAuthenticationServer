package com.mathias8dev.webfluxblogauthenticationserver.domain.utils

inline fun <T> tryOrNull(function: () -> T): T? = runCatching {
    function()
}.getOrNull()