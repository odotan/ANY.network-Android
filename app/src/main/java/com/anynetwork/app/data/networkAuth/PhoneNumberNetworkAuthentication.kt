package com.anynetwork.app.data.networkAuth

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PhoneNumberNetworkAuthentication @Inject constructor() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun sendPhoneNumberVerificationCode(phoneNumber: String, activity: Activity) = callbackFlow {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Timber.d("onVerificationCompleted:$credential")
                trySend(PhoneNumberNetworkAuthenticationEvent.VerificationCompleted(credential))

//                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Timber.w("onVerificationFailed", e)

                val errorMessage = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Invalid phone number."
                    is FirebaseTooManyRequestsException -> "Too many requests. Please try again later."
                    is FirebaseAuthMissingActivityForRecaptchaException -> "Internal error. Please try again."
                    else -> "Verification failed. Please try again."
                }
                trySend(PhoneNumberNetworkAuthenticationEvent.Error(errorMessage)) // Emit an error event
                close(e) // Close the flow with the error

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Timber.d("onCodeSent:$verificationId")
                trySend(PhoneNumberNetworkAuthenticationEvent.CodeSend(verificationId, token))

                // Save verification ID and resending token so we can use them later
//                storedVerificationId = verificationId
//                resendToken = token
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .setActivity(activity)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose {
            Timber.d("Cancelling phone verification callback")
            // No direct method to unregister, but setting an empty callback prevents further emissions
        }
    }

    suspend fun signInWithPhoneAuthCredential(verificationId: String, code: String, activity: Activity) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.d("signInWithCredential:success")

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Timber.w("signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    suspend fun signInWithPhoneAuthCredential(
        verificationId: String,
        code: String
    ): Result<AuthResult> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            val result = auth.signInWithCredential(credential).await()
            Timber.d("signInWithCredential:success")
            Result.success(result)
        } catch (e: Exception) {
            Timber.w("signInWithCredential:failure", e)
            Result.failure(e)
        }
    }
}

sealed class PhoneNumberNetworkAuthenticationEvent {
    data class CodeSend(val verificationId: String, val token: PhoneAuthProvider.ForceResendingToken): PhoneNumberNetworkAuthenticationEvent()
    data class VerificationCompleted(val credential: PhoneAuthCredential): PhoneNumberNetworkAuthenticationEvent()
    data class Error(val message: String) : PhoneNumberNetworkAuthenticationEvent()
}