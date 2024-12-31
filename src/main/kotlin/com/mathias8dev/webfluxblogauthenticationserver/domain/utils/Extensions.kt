package com.mathias8dev.webfluxblogauthenticationserver.domain.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.google.gson.Gson
import com.mathias8dev.webfluxblogauthenticationserver.domain.SpringApplicationContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.regex.Pattern


fun <T> T.toRequestBody(): RequestBody {
    if (this is String) {
        val bytes = toByteArray(Charsets.UTF_8)
        return bytes.toRequestBody("text/plain; charset=utf-8".toMediaTypeOrNull(), 0, bytes.size)
    }
    if (this is File)
        return this.asRequestBody("application/octet-stream".toMediaType())
    return this.toJson().toRequestBody("application/json".toMediaType())
}

fun <T> T.toJson(): String {
    val gson = SpringApplicationContext.getBean(Gson::class.java)
    return if (this is String || this is CharSequence) this.toString()
    else gson.toJson(this)
}


fun <T> T.toJJson(): String = JacksonUtils.objectMapper().writeValueAsString(this)


inline fun <reified T> String.toTypedData(): T {
    val gson: Gson = SpringApplicationContext.getBean(Gson::class.java)
    return gson.fromJson(this, object : TypeReference<T>() {}.type)
}


fun <T> T?.otherwise(value: T): T = this ?: value

inline fun <T> T?.otherwise(block: () -> T): T = this ?: block()

fun String.isEmail(): Boolean {
    return Pattern.compile(
        "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
    ).matcher(this).matches()
}

