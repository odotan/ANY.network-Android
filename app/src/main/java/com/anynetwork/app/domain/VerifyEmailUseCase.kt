package com.anynetwork.app.domain

import com.anynetwork.app.ui.screens.myprofile.EmailNetworkAuthentication
import javax.inject.Inject

class VerifyEmailUseCase @Inject constructor(
    private val emailNetworkAuthentication: EmailNetworkAuthentication
) {
    suspend operator fun invoke(oobCode: String) = emailNetworkAuthentication.verifyEmail(oobCode)
}