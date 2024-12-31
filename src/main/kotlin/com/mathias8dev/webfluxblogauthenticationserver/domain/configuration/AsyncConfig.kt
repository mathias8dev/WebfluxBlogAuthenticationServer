package com.mathias8dev.webfluxblogauthenticationserver.domain.configuration

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor


@EnableAsync
@Configuration
@ConditionalOnProperty(value = ["spring.threads.virtual.enabled"], havingValue = "false")
class AsyncConfig {


    @Bean
    fun taskExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 2
        executor.maxPoolSize = 2
        executor.queueCapacity = 50
        executor.setThreadNamePrefix("AsyncEvents-")
        executor.initialize()
        return executor
    }
}
