package com.mathias8dev.webfluxblogauthenticationserver.dtos

import com.mathias8dev.webfluxblogauthenticationserver.models.Role
import com.mathias8dev.webfluxblogauthenticationserver.models.User

data class PasswordlessUser(
    val id: Long,
    val roles: MutableSet<Role>? = null,
    val username: String,
    val metadata: String? = null,
    var active: Boolean = true,
    var credentialsNonExpired: Boolean = true,
    var accountNonExpired: Boolean = true,
    var accountNonLocked: Boolean = true,
) {

    companion object {
        fun from(user: User): PasswordlessUser {
            return PasswordlessUser(
                id = user.id,
                roles = user.roles,
                username = user.username,
                metadata = user.metadata,
                active = user.active,
                credentialsNonExpired = user.credentialsNonExpired,
                accountNonExpired = user.accountNonExpired,
                accountNonLocked = user.accountNonLocked,
            )
        }
    }
}