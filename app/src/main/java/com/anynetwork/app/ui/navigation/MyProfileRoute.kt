package com.anynetwork.app.ui.navigation

import kotlinx.serialization.Serializable

object MyProfileRoute {
    @Serializable
    data object MyProfile

    @Serializable
    data object ShowMyPhrase

    @Serializable
    data class Connect(
        val mode: String,
        val emailSignInLink: String? = null
    )

    @Serializable
    data class ConnectSuccess(
        val mode: String,
    )
}