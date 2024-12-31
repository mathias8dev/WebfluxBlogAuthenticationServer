package com.mathias8dev.webfluxblogauthenticationserver.domain.annotations

import org.springframework.stereotype.Component


@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class Populator