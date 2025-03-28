package com.anynetwork.app.domain

import com.anynetwork.app.ui.screens.myprofile.EmailNetworkAuthentication
import javax.inject.Inject

class SendSignInLinkUseCase @Inject constructor(
    private val emailNetworkAuthentication: EmailNetworkAuthentication
) {
    suspend fun execute(email: String) {
        emailNetworkAuthentication.sendSignInLink(email)
    }
}