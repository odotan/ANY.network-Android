package com.anynetwork.app.ui.navigation

import kotlinx.serialization.Serializable

object Route {
    @Serializable
    object Splash

    @Serializable
    data class GridPlayground(val mode: Int)

    @Serializable
    object Onboarding

    @Serializable
    object Connect

    @Serializable
    object ContactsPermissions

    @Serializable
    data object Home

    @Serializable
    object MyProfile

    @Serializable
    data class ExternalProfileWithOffset(
        val id: Long,
        val clickOffsetX: Float,
        val clickOffsetY: Float
    )

    @Serializable
    data class ExternalProfile(
        val id: Long,
        val offsetX: Float?,
        val offsetY: Float?,
    )

    @Serializable
    data class NewContact(
        val input: String? = null
    )
}