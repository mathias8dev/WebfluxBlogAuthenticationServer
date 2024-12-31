package com.mathias8dev.webfluxblogauthenticationserver.domain.events.scheduling

import com.mathias8dev.webfluxblogauthenticationserver.repositories.OttRepository
import com.mathias8dev.webfluxblogauthenticationserver.repositories.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant


@Component
class OttClearScheduler(
    private val ottRepository: OttRepository,
    private val userRepository: UserRepository
) {


    @Scheduled(cron = "0 0 0 * * ?") // this will run every day at midnight
    fun clearOtt() {

        val now = Instant.now()  // Capture the current time
        val pageable = Pageable.ofSize(200)  // Define pagination, 20 items per page
        var pageResult = ottRepository.findAllByExpiresAtBefore(now, pageable)

        // Loop through pages and process expired OTTs
        while (pageResult.totalElements > 0) {
            pageResult.forEach {
                // Set the user's one-time token (OTT) to null
                it.user.oneTimeToken = null
                userRepository.save(it.user)
                ottRepository.delete(it)
            }
            // Fetch the next page of expired OTTs
            pageResult = ottRepository.findAllByExpiresAtBefore(now, pageable)
        }
    }
}