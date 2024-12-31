package com.mathias8dev.webfluxblogauthenticationserver.domain.communication.clients

import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface NotificationSenderRestClient {

    @Multipart
    @POST("notifications/notify/order-shipped")
    suspend fun sendOttGenerated(@Part("user") user: RequestBody, @Part("ott") ott: RequestBody)


}