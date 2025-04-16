package com.anynetwork.app.data.networkauth

import android.app.Activity
import android.os.Bundle
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONException
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class FacebookNetworkAuthentication @Inject constructor() {

    val callbackManager = CallbackManager.Factory.create()

    suspend fun signIn(activity: Activity): String = suspendCancellableCoroutine { continuation ->
        val accessToken: AccessToken? = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (isLoggedIn) {
            Timber.i("user already authorized")
            val error = FacebookAuthException.UserAlreadyAuthorized
            continuation.resumeWithException(error)
            return@suspendCancellableCoroutine
        }

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    Timber.i("onSuccess")
                    val accessToken = result.accessToken.token
                    val userId = result.accessToken.userId
                    Timber.i("Token: $accessToken, UserID: $userId")

                    // Optionally get user profile
                    val request = GraphRequest.newMeRequest(result.accessToken) { obj, _ ->
                        try {
                            val name = obj?.getString("name")
                            val link = obj?.getString("link")
                            Timber.i("Name: $name, Link: $link")
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "name, link")
                    request.parameters = parameters
                    request.executeAsync()

                    continuation.resume(accessToken)
                }

                override fun onCancel() {
                    Timber.i("onCancel")
                    continuation.resumeWithException(FacebookAuthException.UserCancelled)
                }

                override fun onError(error: FacebookException) {
                    Timber.i("onError: $error")
                    continuation.resumeWithException(error)
                }
            }
        )

        LoginManager.getInstance().logInWithReadPermissions(
            activity,
            listOf("public_profile")
        )
    }

    suspend fun getAuthorizedUserName(): String = suspendCancellableCoroutine { continuation ->
        val accessToken: AccessToken? = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (!isLoggedIn) {
            continuation.resumeWithException(FacebookAuthException.UserNotLoggedIn)
            return@suspendCancellableCoroutine
        }

        val request = GraphRequest.newMeRequest(accessToken) { obj, _ ->
            try {
                val name = obj?.getString("name")
                if (name != null) {
                    continuation.resume(name)
                } else {
                    continuation.resumeWithException(FacebookAuthException.NameNotFoundInUserProfile)
                }
            } catch (e: JSONException) {
                continuation.resumeWithException(e)
            }
        }

        val parameters = Bundle().apply {
            putString("fields", "name")
        }
        request.parameters = parameters
        request.executeAsync()
    }
}

sealed class FacebookAuthException(message: String) : Exception(message) {
    object UserCancelled : FacebookAuthException("User cancelled login")
    object UserAlreadyAuthorized : FacebookAuthException("User already authorized")
    object UserNotLoggedIn : FacebookAuthException("User is not logged in")
    object NameNotFoundInUserProfile : FacebookAuthException("Name not found in user profile")
}
