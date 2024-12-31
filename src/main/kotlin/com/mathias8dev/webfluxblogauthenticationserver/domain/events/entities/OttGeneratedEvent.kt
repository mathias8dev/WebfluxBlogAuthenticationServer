package com.mathias8dev.webfluxblogauthenticationserver.domain.events.entities

import com.mathias8dev.webfluxblogauthenticationserver.dtos.PasswordlessUser
import com.mathias8dev.webfluxblogauthenticationserver.models.Ott

data class OttGeneratedEvent(
    val user: PasswordlessUser,
    val ott: Ott
)