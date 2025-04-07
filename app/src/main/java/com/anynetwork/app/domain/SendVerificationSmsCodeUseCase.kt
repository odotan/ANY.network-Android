package com.anynetwork.app.domain

import android.app.Activity
import com.anynetwork.app.data.networkAuth.EmailNetworkAuthentication
import com.anynetwork.app.data.networkAuth.PhoneNumberNetworkAuthentication
import com.anynetwork.app.data.networkAuth.PhoneNumberNetworkAuthenticationEvent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendVerificationSmsCodeUseCase @Inject constructor(
    private val phoneNumberNetworkAuthentication: PhoneNumberNetworkAuthentication
) {
    suspend fun execute(phoneNumber: String, activity: Activity): Flow<PhoneNumberNetworkAuthenticationEvent> =
        phoneNumberNetworkAuthentication.sendPhoneNumberVerificationCode(phoneNumber, activity)
}