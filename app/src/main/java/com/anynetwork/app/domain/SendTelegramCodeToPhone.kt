package com.anynetwork.app.domain

import com.anynetwork.app.data.networkauth.TelegramNetworkAuthentication
import javax.inject.Inject

class SendTelegramCodeToPhone @Inject constructor(
    private val telegramNetworkAuthentication: TelegramNetworkAuthentication
) {
    suspend fun execute(phone: String): String? = telegramNetworkAuthentication.sendCode(phone)
}