package com.anynetwork.app.domain

import com.anynetwork.app.data.networkauth.TelegramNetworkAuthentication
import javax.inject.Inject

class VerifyTelegramCodeUseCase @Inject constructor(
    private val telegramNetworkAuthentication: TelegramNetworkAuthentication
) {
    suspend fun execute(code: String) = telegramNetworkAuthentication.verify(code)
}