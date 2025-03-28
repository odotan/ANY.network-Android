package com.anynetwork.app.domain

import com.anynetwork.app.ui.screens.myprofile.EmailNetworkAuthentication
import javax.inject.Inject

class VerifyEmailSignInUseCase @Inject constructor(
    private val emailNetworkAuthentication: EmailNetworkAuthentication
) {
    suspend operator fun invoke(email: String, link: String) = emailNetworkAuthentication.verifySignInLink(email, link)
}