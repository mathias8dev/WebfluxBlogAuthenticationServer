package com.mathias8dev.webfluxblogauthenticationserver.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.time.LocalDateTime


@Entity
@Table(name = "ott")
class Ott(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var token: String,
    var expiresAt: Instant,
    var used: Boolean = false,
    var usedAt: Instant? = null,
    @OneToOne(mappedBy = "oneTimeToken", fetch = FetchType.EAGER)
    @JsonIgnore
    var user: User,
    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(),
) {

    fun copy(
        id: Long = this.id,
        token: String = this.token,
        expiresAt: Instant = this.expiresAt,
        used: Boolean = this.used,
        usedAt: Instant? = this.usedAt,
        user: User = this.user,
        createdAt: LocalDateTime = this.createdAt,
    ) = Ott(
        id = id,
        token = token,
        expiresAt = expiresAt,
        used = used,
        usedAt = usedAt,
        user = user,
        createdAt = createdAt,
    )

    override fun toString(): String {
        return "Ott(id=$id, token='$token', expiresAt=$expiresAt, used=$used, usedAt=$usedAt, createdAt=$createdAt)"
    }
}