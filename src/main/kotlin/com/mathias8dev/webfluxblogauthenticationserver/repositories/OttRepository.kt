package com.mathias8dev.webfluxblogauthenticationserver.repositories

import com.mathias8dev.webfluxblogauthenticationserver.models.Ott
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface OttRepository : JpaRepository<Ott, Long> {
    fun findAllByExpiresAtBefore(expiresAt: Instant, pageable: Pageable): Page<Ott>
}