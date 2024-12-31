package com.mathias8dev.webfluxblogauthenticationserver.loader

import com.mathias8dev.webfluxblogauthenticationserver.domain.annotations.DataLoader
import com.mathias8dev.webfluxblogauthenticationserver.dtos.AuthorityDto
import com.mathias8dev.webfluxblogauthenticationserver.dtos.RoleDto
import com.mathias8dev.webfluxblogauthenticationserver.dtos.UserDto
import com.mathias8dev.webfluxblogauthenticationserver.dtos.toUser
import com.mathias8dev.webfluxblogauthenticationserver.repositories.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder


@DataLoader
class UserDataLoader(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : DataPopulator {

    override fun populate() {
        val user = UserDto(
            roles = mutableSetOf(
                RoleDto(
                    authorities = mutableSetOf(
                        AuthorityDto(
                            name = "SUPER_ADMIN"
                        )
                    ),
                    name = "ADMIN"
                )
            ),
            username = "mathias8dev",
            password = "password"
        ).toUser(passwordEncoder)

        user.active = true
        userRepository.save(user)
    }
}
