package com.mathias8dev.webfluxblogauthenticationserver.repositories

import com.mathias8dev.webfluxblogauthenticationserver.models.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoleRepository : JpaRepository<Role, Int> {
    fun findByName(name: String): Optional<Role>
}