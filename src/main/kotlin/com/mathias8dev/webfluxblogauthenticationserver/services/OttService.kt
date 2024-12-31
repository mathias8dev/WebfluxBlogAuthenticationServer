package com.mathias8dev.webfluxblogauthenticationserver.services

import org.springframework.stereotype.Service
import java.security.SecureRandom


@Service
class OttService {
    fun generateOtt(): String {
        // Generate a random 6 digit number
        val random = SecureRandom()
        val number = random.nextInt(900000) + 100000
        return number.toString()
    }
}