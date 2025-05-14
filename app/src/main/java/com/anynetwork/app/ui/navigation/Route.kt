package com.anynetwork.app.ui.navigation

import kotlinx.serialization.Serializable

object Route {
    @Serializable
    object HashSearch

    @Serializable
    object Splash

    @Serializable
    data class GridPlayground(val mode: Int)

    @Serializable
    object Onboarding

    @Serializable
    object ContactsPermissions

    @Serializable
    data object Home

    @Serializable
    data class MyProfile(
        val offsetX: Float,
        val offsetY: Float
    )

    @Serializable
    data class ExternalProfile(
        val id: Long,
        val offsetX: Float,
        val offsetY: Float,
    )

    @Serializable
    data class ExternalProfileNotExploding(
        val id: Long,
    )

    @Serializable
    data class NewContact(
        val input: String? = null
    )
}