package com.anynetwork.app.domain

import com.anynetwork.app.data.networkauth.PhoneNumberNetworkAuthentication
import javax.inject.Inject

class VerifyPhoneUseCase @Inject constructor(
    private val phoneNumberNetworkAuthentication: PhoneNumberNetworkAuthentication
) {
    suspend fun execute(verificationId: String, code: String) =
        phoneNumberNetworkAuthentication.signInWithPhoneAuthCredential(verificationId, code)
}