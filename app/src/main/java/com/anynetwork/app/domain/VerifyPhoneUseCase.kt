package com.anynetwork.app.domain

import android.app.Activity
import com.anynetwork.app.data.networkAuth.PhoneNumberNetworkAuthentication
import javax.inject.Inject

class VerifyPhoneUseCase @Inject constructor(
    private val phoneNumberNetworkAuthentication: PhoneNumberNetworkAuthentication
) {
    suspend fun execute(verificationId: String, code: String) =
        phoneNumberNetworkAuthentication.signInWithPhoneAuthCredential(verificationId, code)
}