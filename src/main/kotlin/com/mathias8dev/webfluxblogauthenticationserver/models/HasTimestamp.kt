package com.mathias8dev.webfluxblogauthenticationserver.models

import java.time.LocalDateTime

interface HasTimestamp {
    var createdAt: LocalDateTime
    var updatedAt: LocalDateTime
}