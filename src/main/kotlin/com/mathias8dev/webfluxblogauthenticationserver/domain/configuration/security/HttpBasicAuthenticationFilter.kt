package com.mathias8dev.webfluxblogauthenticationserver.domain.configuration.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolderStrategy
import org.springframework.security.web.authentication.*
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.*

class HttpBasicAuthenticationConverter : AuthenticationConverter {
    override fun convert(request: HttpServletRequest): Authentication? {
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith("Basic ")) {
            val credentials = String(Base64.getDecoder().decode(header.substring(6))).split(":")
            val username = credentials[0]
            val password = credentials[1]
            println("The credentials is $credentials")

            return HttpBasicAuthentication(
                UsernamePassword(
                    username,
                    password
                )
            )
        }

        return null
    }
}

data class UsernamePassword(val username: String, val password: String)

data class HttpBasicAuthentication(
    private val user: UsernamePassword
) : Authentication {
    override fun getName(): String {
        return user.username
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf()
    }

    override fun getCredentials(): Any {
        return user
    }

    override fun getDetails(): Any {
        return user
    }

    override fun getPrincipal(): Any {
        return user.username
    }

    override fun isAuthenticated(): Boolean {
        return true
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {

    }

}

class HttpBasicAuthenticationManager(
    private val environment: Environment
) : AuthenticationManager {

    override fun authenticate(authentication: Authentication): Authentication? {
        val registeredCredentials = getRegisteredCredentials()
        val username = authentication.principal.toString()
        val password = authentication.credentials.toString()
        if (registeredCredentials.containsKey(username) && registeredCredentials[username] == password) {
            return HttpBasicAuthentication(UsernamePassword(username, password))
        }
        return null
    }

    private fun getRegisteredCredentials(): Map<String, String> {
        val credentialsMap = mutableMapOf<String, String>()
        val credentials: String? = environment["mesmedocs.authentication.registered-client-credentials"]
        credentials?.split(",")?.forEach { credential ->
            val parts = credential.split(":")
            if (parts.size == 2) {
                credentialsMap[parts[0].trim()] = parts[1].trim()
            }
        }
        return credentialsMap
    }
}


class HttpBasicAuthenticationFilter : OncePerRequestFilter() {
    private var securityContextHolderStrategy: SecurityContextHolderStrategy? = null
    private val authenticationConverter: AuthenticationConverter = BasicAuthenticationConverter()
    private var successHandler: AuthenticationSuccessHandler? = null
    private var failureHandler: AuthenticationFailureHandler? = null
    private var securityContextRepository: SecurityContextRepository? = null
    private val authenticationManager = HttpBasicAuthenticationManager(environment)


    init {
        this.securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy()
        this.successHandler = SavedRequestAwareAuthenticationSuccessHandler()
        this.failureHandler = AuthenticationEntryPointFailureHandler(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        this.securityContextRepository = RequestAttributeSecurityContextRepository()
    }


    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.trace("The request is $request")
        logger.trace("The servletPath is ${request.servletPath}")
        if (request.servletPath != "/authentication-server/find/by/username") {
            filterChain.doFilter(request, response)
        } else {
            try {
                val authenticationResult = this.attemptAuthentication(request, response)

                val session = request.getSession(false)
                if (session != null) {
                    request.changeSessionId()
                }

                this.successfulAuthentication(request, response, filterChain, authenticationResult)
            } catch (var6: AuthenticationException) {
                this.unsuccessfulAuthentication(request, response, var6)
            }
        }
    }

    @Throws(IOException::class, ServletException::class)
    private fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        securityContextHolderStrategy!!.clearContext()
        failureHandler!!.onAuthenticationFailure(request, response, failed)
    }

    @Throws(IOException::class, ServletException::class)
    private fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authentication: Authentication
    ) {
        val context =
            securityContextHolderStrategy!!.createEmptyContext()
        context.authentication = authentication
        securityContextHolderStrategy!!.context = context
        securityContextRepository!!.saveContext(context, request, response)
        successHandler!!.onAuthenticationSuccess(request, response, chain, authentication)
    }

    @Throws(AuthenticationException::class, ServletException::class)
    private fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {

        val authenticationResult = authenticationConverter.convert(request)?.let { authentication ->
            authenticationManager.authenticate(authentication)
        }
        if (authenticationResult == null) {
            throw AccessDeniedException("Access Denied")
        } else {
            return authenticationResult
        }
    }
}