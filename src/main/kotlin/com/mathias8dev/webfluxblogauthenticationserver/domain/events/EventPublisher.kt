package com.mathias8dev.webfluxblogauthenticationserver.domain.events

import com.mathias8dev.webfluxblogauthenticationserver.domain.events.entities.OttGeneratedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component


@Component
class EventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) {


    fun publishOttGeneratedEvent(event: OttGeneratedEvent) {
        applicationEventPublisher.publishEvent(event)
    }


}