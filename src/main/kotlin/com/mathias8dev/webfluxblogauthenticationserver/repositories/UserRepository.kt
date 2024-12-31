package com.mathias8dev.webfluxblogauthenticationserver.repositories

import com.mathias8dev.webfluxblogauthenticationserver.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*


interface UserRepository : JpaSpecificationExecutor<User>, JpaRepository<User, UUID> {
    fun findByUsername(username: String): Optional<User>
    fun existsByUsernameIgnoreCase(username: String): Boolean
    fun existsByEmailIgnoreCase(username: String): Boolean
    fun existsByUsernameIgnoreCaseOrEmailIgnoreCase(username: String, email: String = username): Boolean
    fun findByUsernameIgnoreCaseOrEmailIgnoreCase(username: String, email: String = username): Optional<User>
}