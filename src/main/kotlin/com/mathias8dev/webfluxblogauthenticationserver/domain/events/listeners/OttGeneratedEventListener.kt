package com.mathias8dev.webfluxblogauthenticationserver.domain.events.listeners

import com.mathias8dev.webfluxblogauthenticationserver.domain.communication.clients.NotificationSenderRestClient
import com.mathias8dev.webfluxblogauthenticationserver.domain.events.entities.OttGeneratedEvent
import com.mathias8dev.webfluxblogauthenticationserver.domain.utils.toJJson
import com.mathias8dev.webfluxblogauthenticationserver.domain.utils.toRequestBody
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component


@Component
class OttGeneratedEventListener(
    private val notificationSenderRestClient: NotificationSenderRestClient
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Async
    @EventListener
    fun handleEvent(event: OttGeneratedEvent) {
        runBlocking<Unit> {
            logger.debug("New event received: {}", event.toJJson())
            kotlin.runCatching {
                notificationSenderRestClient.sendOttGenerated(
                    user = event.user.toRequestBody(),
                    ott = event.ott.toRequestBody()
                )
            }
        }
    }
}