package com.anynetwork.app.data.contacts

import com.anynetwork.app.model.Contact

interface ContactsDataSource {
    suspend fun getContacts(): List<Contact>
    suspend fun getContact(contactId: Long): Contact?
    suspend fun editContact(contactId: Long, contact: Contact): Boolean
    suspend fun addContact(contact: Contact): Boolean
    suspend fun favoriteContact(contactId: Long, isFavorite: Boolean): Boolean
    suspend fun deleteAllContacts(): Boolean
}