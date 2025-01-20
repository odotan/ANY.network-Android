package com.anynetwork.app.ui.screens.home

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import com.anynetwork.app.R
import com.anynetwork.app.model.Contact
import com.anynetwork.app.ui.theme.EmailColor
import com.anynetwork.app.ui.theme.GreenColor
import com.anynetwork.app.ui.theme.YellowColor

@Stable
@Immutable
sealed class GridItem(val contact: Contact?, val badge: Badge? = null) {
    class FavoritedContactGridItem(contact: Contact): GridItem(contact, Badge.FavoriteBadge)
    class InteractionGridItem(contact: Contact, val interactionId: Long, badge: Badge?): GridItem(contact, badge) {
        override fun equals(other: Any?): Boolean {
            if (other !is InteractionGridItem || !super.equals(other)) return false
            val identicalInteractionIds = interactionId == other.interactionId

            return identicalInteractionIds
        }
    }
    class SearchGridItem(contact: Contact): GridItem(contact, null)
    class EmptyGridItem: GridItem(null, null)

    @Stable
    @Immutable
    sealed class Badge(open val color: Color, open val iconResId: Int, open val iconColorFilter: ColorFilter? = null) {
        data object FavoriteBadge: Badge(
            color = YellowColor,
            iconResId = R.drawable.ic_star_filled,
            iconColorFilter = ColorFilter.tint(color = Color.White)
        )
        data object EmailBadge: Badge(
            color = EmailColor,
            iconResId = R.drawable.ic_email,
        )
        data object PhoneBadge: Badge(
            color = GreenColor,
            iconResId = R.drawable.ic_phone,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other !is GridItem) return false

        val identicalContacts = contact == other.contact
        val identicalBadges = badge == other.badge

        return identicalContacts && identicalBadges
    }
}