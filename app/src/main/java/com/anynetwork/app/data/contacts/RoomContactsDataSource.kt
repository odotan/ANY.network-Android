package com.anynetwork.app.data.contacts

import com.anynetwork.app.db.dao.ContactDao
import com.anynetwork.app.db.entity.unwrap
import com.anynetwork.app.db.entity.wrap
import com.anynetwork.app.model.Contact
import com.anynetwork.app.ui.utils.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomContactsDataSource(private val contactDao: ContactDao): ContactsDataSource {
    override suspend fun getContacts(): List<Contact> = withContext(Dispatchers.IO) {
        return@withContext contactDao.getAllContacts().map {
            it.unwrap()
        }
    }

    override suspend fun getContact(contactId: Long): Contact? = withContext(Dispatchers.IO) {
        return@withContext contactDao.getContact(contactId)?.unwrap()
    }

    override suspend fun editContact(contactId: Long, contact: Contact): Boolean = withContext(Dispatchers.IO) {
        val rowsUpdated = contactDao.updateContact(contact.wrap())
        return@withContext rowsUpdated > 0
    }

    override suspend fun addContact(contact: Contact) = withContext(Dispatchers.IO) {
        contactDao.insertContact(contact.wrap())
        true
    }

    override suspend fun favoriteContact(contactId: Long, isFavorite: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            val rowsUpdated = contactDao.updateFavoriteStatus(contactId, isFavorite)
            return@withContext rowsUpdated > 0
        }

    override suspend fun deleteAllContacts(): Boolean {
        contactDao.deleteAllContacts()
        return true
    }
}