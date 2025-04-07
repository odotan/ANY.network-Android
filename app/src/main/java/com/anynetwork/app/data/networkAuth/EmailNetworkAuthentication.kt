package com.anynetwork.app.data.networkAuth

import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EmailNetworkAuthentication @Inject constructor() {

    private val actionCodeSettings: ActionCodeSettings = ActionCodeSettings.newBuilder()
        .setUrl("https://anynetwork.page.link")
        .setHandleCodeInApp(true)
        .setAndroidPackageName(
            "com.anynetwork.app",
            true,
            null
        )
        .build()

    private var email: String? = null

    init {
        signOut()
    }

    suspend fun sendSignInLink(toEmail: String) {
        FirebaseAuth.getInstance().sendSignInLinkToEmail(toEmail, actionCodeSettings).await()
        this.email = toEmail
    }

    suspend fun verifySignInLink(email: String, link: String): String {
        return if (FirebaseAuth.getInstance().isSignInWithEmailLink(link)) {
            val result = FirebaseAuth.getInstance().signInWithEmailLink(email, link).await()
            result.user?.email ?: throw EmailError.Unknown
        } else {
            throw EmailError.Unknown
        }
    }

    fun verifyEmail(oobCode: String) = flow {
        emit(VerificationState.Loading)
        try {
            FirebaseAuth.getInstance().applyActionCode(oobCode).await()
            emit(VerificationState.Success)
        } catch (e: Exception) {
            emit(VerificationState.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO) // Run on background thread

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    sealed class EmailError(override val message: String?) : Throwable() {
        data object MissingEmail : EmailError("Missing Email")
        data object Unknown : EmailError("Unknown Error")
    }

    sealed class VerificationState {
        object Loading : VerificationState()  // Represents the loading state (while verification is in progress)
        object Success : VerificationState()  // Represents success after email verification
        data class Error(val message: String) : VerificationState()  // Represents failure with an error message
    }
}