package com.anynetwork.app.data.contacts

import com.anynetwork.app.di.PhoneContactsDataSourceQualifier
import com.anynetwork.app.di.RoomContactsDataSourceQualifier
import com.anynetwork.app.model.Contact
import com.anynetwork.app.utils.PermissionsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ContactsRepository @Inject constructor(
    @PhoneContactsDataSourceQualifier private val phoneDataSource: ContactsDataSource,
    @RoomContactsDataSourceQualifier private val roomDataSource: ContactsDataSource,
    private val permissionsManager: PermissionsManager
) {
    suspend fun getContacts(): Flow<List<Contact>> = flow {
        // Emit contacts from Room first
        val roomContacts = roomDataSource.getContacts()
        emit(roomContacts)

        // If permissions are granted, fetch from the phone data source
        if (permissionsManager.isContactsPermissionGranted()) {
            val phoneContacts = phoneDataSource.getContacts()

            // Emit contacts from the phone data source
            emit(phoneContacts)

            // Update Room database with new contacts from phone (optional sync)
            roomDataSource.deleteAllContacts()
            phoneContacts.forEach { roomDataSource.addContact(it) }

        }
    }

    suspend fun getContact(contactId: Long): Contact? {
        return if (permissionsManager.isContactsPermissionGranted()) {
            phoneDataSource.getContact(contactId)
        } else {
            roomDataSource.getContact(contactId)
        }
    }

    suspend fun editContact(contactId: Long, contact: Contact): Boolean {
        return if (permissionsManager.isContactsPermissionGranted()) {
            roomDataSource.editContact(contactId, contact)
            phoneDataSource.editContact(contactId, contact)
        } else {
            roomDataSource.editContact(contactId, contact)
        }
    }

    suspend fun addContact(contact: Contact): Boolean =
        if (permissionsManager.isContactsPermissionGranted()) {
            phoneDataSource.addContact(contact)
        } else {
            roomDataSource.addContact(contact)
        }

    suspend fun favoriteContact(contactId: Long, isFavorite: Boolean): Boolean {
        return if (permissionsManager.isContactsPermissionGranted()) {
            roomDataSource.favoriteContact(contactId, isFavorite)
            phoneDataSource.favoriteContact(contactId, isFavorite)
        } else {
            roomDataSource.favoriteContact(contactId, isFavorite)
        }
    }
}