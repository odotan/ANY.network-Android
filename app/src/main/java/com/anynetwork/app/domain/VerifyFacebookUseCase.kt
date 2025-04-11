package com.anynetwork.app.domain

import android.app.Activity
import com.anynetwork.app.data.networkauth.FacebookNetworkAuthentication
import javax.inject.Inject

class VerifyFacebookUseCase @Inject constructor(
    private val facebookNetworkAuthentication: FacebookNetworkAuthentication
) {
    suspend fun execute(activity: Activity) = facebookNetworkAuthentication.signIn(activity)
}