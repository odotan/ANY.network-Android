package com.anynetwork.app.data.contacts

import com.anynetwork.app.di.PhoneContactsDataSourceQualifier
import com.anynetwork.app.di.RoomContactsDataSourceQualifier
import com.anynetwork.app.model.Contact
import com.anynetwork.app.ui.utils.log
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
        var roomContacts = roomDataSource.getContacts()
        emit(roomContacts)

        // If permissions are granted, fetch from the phone data source
        if (permissionsManager.isContactsPermissionGranted()) {
            val phoneContacts = phoneDataSource.getContacts()

            // Emit contacts from the phone data source

            // Update Room database with new contacts from phone (optional sync)
            phoneContacts.forEach { roomDataSource.addContact(it) }

            roomContacts = roomDataSource.getContacts().apply {
                size.log { "roomContacts size" }
            }
            emit(roomContacts)
        }
    }

    suspend fun getContact(contactId: Long): Contact? {
        return if (permissionsManager.isContactsPermissionGranted()) {
            roomDataSource.getContact(contactId)
//            phoneDataSource.getContact(contactId)
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
//            roomDataSource.addContact(contact)
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