package com.mathias8dev.webfluxblogauthenticationserver.dtos

import com.mathias8dev.webfluxblogauthenticationserver.models.Authority
import com.mathias8dev.webfluxblogauthenticationserver.models.Role
import com.mathias8dev.webfluxblogauthenticationserver.models.User
import org.springframework.security.crypto.password.PasswordEncoder


data class UserDto(
    val roles: MutableSet<RoleDto>? = null,
    val username: String?,
    val email: String? = null,
    val password: String?,
    val metadata: String? = null,
)


data class RoleDto(
    val name: String,
    val authorities: MutableSet<AuthorityDto>
)

fun RoleDto.toRole() = Role(
    name = name,
    authorities = authorities.map { it.toAuthority() }.toMutableSet()
)

data class AuthorityDto(
    val name: String
)


fun AuthorityDto.toAuthority() = Authority(
    name = name
)

fun UserDto.toUser(passwordEncoder: PasswordEncoder): User {
    return User(
        roles = roles?.map { it.toRole() }?.toMutableSet() ?: mutableSetOf(
            Role(
                authorities = mutableSetOf(
                    Authority(
                        name = "APP_USER"
                    )
                ),
                name = "USER"
            )
        ),
        username = username!!,
        email = email,
        password = passwordEncoder.encode(password!!),
        metadata = metadata
    )
}