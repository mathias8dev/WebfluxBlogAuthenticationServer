package com.mathias8dev.webfluxblogauthenticationserver.domain.communication.configuration

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mathias8dev.webfluxblogauthenticationserver.domain.communication.adapters.gson.InstantTypeAdapter
import com.mathias8dev.webfluxblogauthenticationserver.domain.communication.adapters.gson.LocalDateTimeTypeAdapter
import com.mathias8dev.webfluxblogauthenticationserver.domain.communication.adapters.gson.LocalDateTypeAdapter
import com.mathias8dev.webfluxblogauthenticationserver.domain.communication.adapters.gson.ZonedDateTimeTypeAdapter
import com.mathias8dev.webfluxblogauthenticationserver.domain.communication.clients.AuthorizationServerRestClient
import com.mathias8dev.webfluxblogauthenticationserver.domain.communication.clients.NotificationSenderRestClient
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime


@Configuration
class ClientConfiguration {

    @Value("\${webfluxblog.gateway.api-url}")
    private lateinit var gatewayApiUrl: String

    @LoadBalanced
    @Bean
    fun okHttpClient(
        okHttpClientBuilder: OkHttpClient.Builder,
    ): OkHttpClient {

        return okHttpClientBuilder
            .build()
    }

    @Bean
    fun okHttpClientBuilder(): OkHttpClient.Builder {
        val cache = Cache(File("cache"), 10 * 1024 * 1024) // 10MB cache file
        val cacheInterceptor = CacheInterceptor()
        val loggingInterceptor = HttpLoggingInterceptor()
        val authInterceptor = AuthInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(loggingInterceptor)
            .addNetworkInterceptor(cacheInterceptor)
            .addInterceptor(authInterceptor)


    }

    @Bean
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
            .registerTypeAdapter(ZonedDateTime::class.java, ZonedDateTimeTypeAdapter())
            .registerTypeAdapter(Instant::class.java, InstantTypeAdapter())
            .setLenient()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .create()
    }

    @Bean
    fun gsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }


    @Bean
    fun notificationSenderRestClient(
        okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): NotificationSenderRestClient {
        return Retrofit.Builder().client(okHttpClient)
            .baseUrl("$gatewayApiUrl/")
            .addConverterFactory(converterFactory)
            .build().create(NotificationSenderRestClient::class.java)
    }


    @Bean
    fun authorizationServerRestClient(
        okHttpClientBuilder: OkHttpClient.Builder,
        gson: Gson
    ): AuthorizationServerRestClient {
        val okHttpClient = okHttpClientBuilder
            .addInterceptor(BasicAuthInterceptor())
            .build()


        return Retrofit.Builder().client(okHttpClient)
            .baseUrl("$gatewayApiUrl/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(AuthorizationServerRestClient::class.java)
    }
}