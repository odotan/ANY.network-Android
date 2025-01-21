package com.anynetwork.app.ui.screens.home

import com.anynetwork.app.model.Contact
import com.anynetwork.app.model.Interaction

data class ContactsListItem(val contact: Contact, val lastInteraction: Interaction) {
}