package com.mathias8dev.webfluxblogauthenticationserver.domain.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.security.jackson2.CoreJackson2Module


object JacksonUtils {

    fun objectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(CoreJackson2Module())
        objectMapper.registerModule(JavaTimeModule())

        return objectMapper
    }


}