package com.mathias8dev.webfluxblogauthenticationserver.controllers

import com.mathias8dev.webfluxblogauthenticationserver.dtos.PasswordlessUser
import com.mathias8dev.webfluxblogauthenticationserver.dtos.UserDto
import com.mathias8dev.webfluxblogauthenticationserver.models.User
import com.mathias8dev.webfluxblogauthenticationserver.services.UserService
import jakarta.annotation.Nullable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/authentication-server")
class AuthenticationController(
    private val userService: UserService
) {


    @GetMapping("/hello")
    fun hello(): String {
        return "hello world"
    }

    @PostMapping("/find/by/username")
    @PreAuthorize("hasAnyAuthority('SCOPE_USER.FULL_READ')")
    fun findByUsername(@RequestPart username: String): User {
        return userService.findByUsernameIgnoreCaseOrEmailIgnoreCase(username = username)
    }

    @PostMapping("/register-user")
    @PreAuthorize("hasAnyAuthority('SCOPE_USER.CREATE', 'ROLE_WEBFLUXBLOG_ADMIN', 'SCOPE_WEBFLUXBLOG.APP', 'SCOPE_WEBFLUXBLOG.ADMIN')")
    fun registerUser(@RequestPart userDto: UserDto): PasswordlessUser {
        return userService.registerUser(userDto)
    }

    @PostMapping("/update-registered-user")
    fun updateRegisteredUser(@RequestPart username: String, @RequestPart userDto: UserDto): User {
        return userService.updateRegisteredUser(username, userDto)
    }

    @DeleteMapping("/delete-registered-user")
    fun deleteRegisteredUser(@RequestPart username: String) {
        userService.deleteRegisteredUser(username)
    }


    @DeleteMapping("/delete-all")
    @PreAuthorize("hasAuthority('SCOPE_SUPER_ADMIN')")
    fun deleteAll() {
        return userService.deleteAll()
    }

    @PostMapping("/configure-user")
    fun configureUser(
        @RequestPart username: String,
        @RequestPart metadata: String? = null,
        @RequestPart @Nullable active: Boolean? = null,
        @RequestPart @Nullable credentialsNonExpired: Boolean? = null,
        @RequestPart @Nullable accountNonExpired: Boolean? = null,
        @RequestPart @Nullable accountNonLocked: Boolean? = null
    ): PasswordlessUser {
        return userService.configureUser(
            username = username,
            metadata = metadata,
            active = active,
            accountNonLocked = accountNonLocked,
            accountNonExpired = accountNonExpired,
            credentialsNonExpired = credentialsNonExpired
        )
    }


    @PostMapping("/find-all/by/authorities")
    fun findAllByAuthorities(@RequestPart authorities: List<String>): List<PasswordlessUser> {
        return userService.findAllByAuthorities(authorities)
    }

    @PostMapping("/generate-ott")
    fun generateOtt(@RequestPart username: String) {
        return userService.generateOtt(username)
    }

    @PostMapping("/validate-ott-and-get-user")
    @PreAuthorize("hasAnyAuthority('SCOPE_USER.FULL_READ')")
    fun validateOttAndGetUser(@RequestPart username: String, @RequestPart ott: String): User {
        return userService.validateOttAndGetUser(username, ott)
    }
}