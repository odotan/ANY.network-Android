package com.anynetwork.app.domain

import com.anynetwork.app.data.networkauth.FacebookNetworkAuthentication
import javax.inject.Inject

class FetchFacebookProfileNameUseCase @Inject constructor(
    private val facebookNetworkAuthentication: FacebookNetworkAuthentication
) {
    suspend fun execute() = facebookNetworkAuthentication.getAuthorizedUserName()
}