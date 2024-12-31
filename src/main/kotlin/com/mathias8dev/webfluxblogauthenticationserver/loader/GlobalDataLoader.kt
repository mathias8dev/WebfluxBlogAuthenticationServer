package com.mathias8dev.webfluxblogauthenticationserver.loader

import com.mathias8dev.webfluxblogauthenticationserver.domain.annotations.Populator
import com.mathias8dev.webfluxblogauthenticationserver.services.UserService
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent


@Populator
class GlobalDataLoader(
    private val userDataLoader: UserDataLoader,
    private val userService: UserService,
) : ApplicationListener<ContextRefreshedEvent?> {


    private var alreadySetup = false


    private val isDataAlreadyLoaded: Boolean
        get() = userService.findAll().isNotEmpty()

    override fun onApplicationEvent(event: ContextRefreshedEvent) {

        if (isDataAlreadyLoaded) alreadySetup = true
        if (alreadySetup) return

        userDataLoader.populate()
        alreadySetup = true
    }
}