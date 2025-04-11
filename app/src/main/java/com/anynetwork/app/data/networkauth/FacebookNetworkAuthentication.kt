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

    private val callbackManager = CallbackManager.Factory.create()

    suspend fun signIn(activity: Activity): String = suspendCancellableCoroutine { continuation ->
        LoginManager.getInstance().logInWithReadPermissions(
            activity,
            listOf("public_profile")
        )

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
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
                    parameters.putString("fields", "name,link")
                    request.parameters = parameters
                    request.executeAsync()

                    continuation.resume(accessToken)
                }

                override fun onCancel() {
                    continuation.resumeWithException(FacebookAuthException.UserCancelled)
                }

                override fun onError(error: FacebookException) {
                    continuation.resumeWithException(error)
                }
            }
        )
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}

sealed class FacebookAuthException(message: String) : Exception(message) {
    object UserCancelled : FacebookAuthException("User cancelled login")
}
