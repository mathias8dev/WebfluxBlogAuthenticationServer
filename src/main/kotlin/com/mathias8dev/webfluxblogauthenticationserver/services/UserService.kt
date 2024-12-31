package com.mathias8dev.webfluxblogauthenticationserver.services


import com.mathias8dev.webfluxblogauthenticationserver.domain.events.EventPublisher
import com.mathias8dev.webfluxblogauthenticationserver.domain.events.entities.OttGeneratedEvent
import com.mathias8dev.webfluxblogauthenticationserver.domain.utils.otherwise
import com.mathias8dev.webfluxblogauthenticationserver.dtos.*
import com.mathias8dev.webfluxblogauthenticationserver.exceptions.HttpException
import com.mathias8dev.webfluxblogauthenticationserver.models.Ott
import com.mathias8dev.webfluxblogauthenticationserver.models.User
import com.mathias8dev.webfluxblogauthenticationserver.repositories.UserRepository
import com.mathias8dev.webfluxblogauthenticationserver.specifications.UserSpecification
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val ottService: OttService,
    private val eventPublisher: EventPublisher,
    environment: Environment
) {

    val ottDefaultDurationMillis = environment.getProperty("webfluxblog.ott.default-duration-millis", Long::class.java, 300000)

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun findByUsernameIgnoreCaseOrEmailIgnoreCase(username: String): User {
        return userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(username).orElseThrow {
            Exception("Unable to find user: $username")
        }
    }

    fun save(user: User): User {
        return userRepository.save(user)
    }

    fun findAll(): List<User> {
        return userRepository.findAll()
    }

    fun deleteAll() {
        userRepository.deleteAll()
    }

    fun registerUser(userDto: UserDto): PasswordlessUser {
        val user = userDto.toUser(passwordEncoder).apply {
            active = false // TODO deactivate this

        }
        return PasswordlessUser.from(save(user))
    }

    fun updateRegisteredUser(username: String, userDto: UserDto): User {
        val user = findByUsernameIgnoreCaseOrEmailIgnoreCase(username)
        userDto.password?.let {
            if (!passwordEncoder.matches(it, user.password)) {
                user.password = passwordEncoder.encode(it)
            }
        }

        userDto.username?.let {
            if (userRepository.existsByUsernameIgnoreCase(it)) {
                throw Exception("Username already taken: $username")
            }

            user.username = it
        }

        userDto.metadata?.let {
            user.metadata = it
        }

        userDto.email?.let {
            if (userRepository.existsByEmailIgnoreCase(it)) {
                throw Exception("Email already taken: $username")
            }

            user.email = it
        }

        userDto.roles?.let { roles ->
            for (roleDto in roles) {
                if (user.roles.find { it.name == roleDto.name } == null) {
                    user.roles.add(roleDto.toRole())
                } else {
                    user.roles.forEach {
                        if (it.name == roleDto.name) {
                            roleDto.authorities.forEach { authorityDto ->
                                if (it.authorities.find { authority -> authority.name == authorityDto.name } == null) {
                                    it.authorities.add(authorityDto.toAuthority())
                                }
                            }
                        }
                    }

                }
            }
        }

        return save(user)
    }

    fun configureUser(
        username: String,
        metadata: String? = null,
        active: Boolean? = null,
        credentialsNonExpired: Boolean? = null,
        accountNonExpired: Boolean? = null,
        accountNonLocked: Boolean? = null
    ): PasswordlessUser {
        val user = findByUsernameIgnoreCaseOrEmailIgnoreCase(username)
        active?.let {
            user.active = it
        }

        credentialsNonExpired?.let {
            user.credentialsNonExpired = it
        }

        accountNonExpired?.let {
            user.accountNonExpired = it
        }

        accountNonLocked?.let {
            user.accountNonLocked = it
        }

        metadata?.let {
            user.metadata = metadata
        }

        return PasswordlessUser.from(save(user))
    }

    fun deleteRegisteredUser(username: String) {
        val user = findByUsernameIgnoreCaseOrEmailIgnoreCase(username)
        userRepository.delete(user)
    }

    fun findAllByAuthorities(authorities: List<String>): List<PasswordlessUser> {
        if (authorities.isEmpty())
            return emptyList()

        return userRepository.findAll(UserSpecification.hasAuthorityIn(authorities))
            .distinctBy { it.id }
            .map { PasswordlessUser.from(it) }
    }

    fun validateOttAndGetUser(username: String, ott: String): User {
        val user = findByUsernameIgnoreCaseOrEmailIgnoreCase(username)
        user.oneTimeToken?.let {
            val now = Instant.now()
            if (it.expiresAt.isBefore(now)) {
                throw HttpException(HttpStatus.BAD_REQUEST, "One time token expired")
            }

            if (!passwordEncoder.matches(ott, it.token)) {
                throw HttpException(HttpStatus.BAD_REQUEST, "One time token incorrect")
            }

            if (it.used) {
                throw HttpException(HttpStatus.BAD_REQUEST, "One time token already used")
            }

            user.oneTimeToken!!.usedAt = now
            user.oneTimeToken!!.used = true
        }.otherwise {
            throw HttpException(HttpStatus.BAD_REQUEST, "User has no one time token")
        }

        return user
    }

    fun generateOtt(username: String) {
        val user = findByUsernameIgnoreCaseOrEmailIgnoreCase(username)
        val rawGeneratedOtt = ottService.generateOtt()
        val encodedGeneratedOtt = passwordEncoder.encode(rawGeneratedOtt)
        user.oneTimeToken = user.oneTimeToken?.apply {
            token = encodedGeneratedOtt
            expiresAt = Instant.now().plusMillis(ottDefaultDurationMillis)
            used = false
            usedAt = null
        } ?: Ott(
            token = encodedGeneratedOtt,
            expiresAt = Instant.now().plusMillis(ottDefaultDurationMillis),
            used = false,
            user = user,
            usedAt = null,
        )

        val savedUser = save(user)

        logger.debug("The raw generatedToken value is $rawGeneratedOtt")

        eventPublisher.publishOttGeneratedEvent(
            OttGeneratedEvent(
                user = PasswordlessUser.from(savedUser),
                ott = user.oneTimeToken!!.copy(token = rawGeneratedOtt)
            )
        )
    }
}