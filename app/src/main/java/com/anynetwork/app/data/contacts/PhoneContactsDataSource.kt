package com.anynetwork.app.data.contacts

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import com.anynetwork.app.model.Contact
import com.anynetwork.app.ui.utils.log
import contacts.core.Contacts
import contacts.core.entities.EmailEntity
import contacts.core.entities.PhoneEntity
import contacts.core.equalTo
import contacts.core.util.PhotoData
import contacts.core.util.addEmail
import contacts.core.util.addPhone
import contacts.core.util.emailList
import contacts.core.util.options
import contacts.core.util.organizations
import contacts.core.util.phoneList
import contacts.core.util.removeAllEmails
import contacts.core.util.removeAllPhones
import contacts.core.util.setName
import contacts.core.util.setOptions
import contacts.core.util.setOrganization
import contacts.core.util.setPhoto
import contacts.permissions.insertWithPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class PhoneContactsDataSource(private val context: Context): ContactsDataSource {
    override suspend fun getContacts(): List<Contact> = withContext(Dispatchers.IO) {
        return@withContext Contacts(context).query().find().map { it ->
            Contact(
                id = it.id,
                name = it.displayNamePrimary ?: "",
                company = it.organizations().firstOrNull()?.company,
                phones = it.phoneList().map {
                    Contact.Phone(
                        type = when (it.type) {
                            PhoneEntity.Type.MOBILE -> Contact.Phone.Type.Mobile
                            PhoneEntity.Type.HOME -> Contact.Phone.Type.Home
                            PhoneEntity.Type.WORK -> Contact.Phone.Type.Work
                            PhoneEntity.Type.MAIN -> Contact.Phone.Type.Main
                            PhoneEntity.Type.FAX_WORK -> Contact.Phone.Type.WorkFax
                            PhoneEntity.Type.FAX_HOME -> Contact.Phone.Type.HomeFax
                            PhoneEntity.Type.PAGER -> Contact.Phone.Type.Pager
                            else -> Contact.Phone.Type.Other
                        },
                        value = it.number ?: ""
                    )
                },
                emails = it.emailList().map {
                    Contact.Email(
                        type = when (it.type) {
                            EmailEntity.Type.HOME -> Contact.Email.Type.Home
                            EmailEntity.Type.WORK -> Contact.Email.Type.Work
                            else -> Contact.Email.Type.Other
                        },
                        value = it.address ?: ""
                    )
                },
                avatarUri = it.photoUri?.toString(),
                isFavorite = it.options?.starred ?: false
            )
        }
    }

    override suspend fun getContact(contactId: Long): Contact? = withContext(Dispatchers.IO) {
        return@withContext Contacts(context).query()
            .where {
                (Contact.Id equalTo contactId.toString())
            }
            .find()
            .map {
                Contact(
                    id = it.id,
                    name = it.displayNamePrimary ?: "",
                    company = it.organizations().firstOrNull()?.company,
                    phones = it.phoneList().map {
                        Contact.Phone(
                            type = when (it.type) {
                                PhoneEntity.Type.MOBILE -> Contact.Phone.Type.Mobile
                                PhoneEntity.Type.HOME -> Contact.Phone.Type.Home
                                PhoneEntity.Type.WORK -> Contact.Phone.Type.Work
                                PhoneEntity.Type.MAIN -> Contact.Phone.Type.Main
                                PhoneEntity.Type.FAX_WORK -> Contact.Phone.Type.WorkFax
                                PhoneEntity.Type.FAX_HOME -> Contact.Phone.Type.HomeFax
                                PhoneEntity.Type.PAGER -> Contact.Phone.Type.Pager
                                else -> Contact.Phone.Type.Other
                            },
                            value = it.number ?: ""
                        )
                    },
                    emails = it.emailList().map {
                        Contact.Email(
                            type = when (it.type) {
                                EmailEntity.Type.HOME -> Contact.Email.Type.Home
                                EmailEntity.Type.WORK -> Contact.Email.Type.Work
                                else -> Contact.Email.Type.Other
                            },
                            value = it.address ?: ""
                        )
                    },
                    avatarUri = it.photoUri?.toString(),
                    isFavorite = it.options()?.starred ?: false,
                )
            }
            .firstOrNull()
    }

    override suspend fun editContact(contactId: Long, contact: Contact): Boolean = withContext(Dispatchers.IO) {
        val updateResult = Contacts(context)
            .update()
            .contacts(Contacts(context).query()
                .where {
                    (Contact.Id equalTo contactId.toString())
                }
                .find()
                .first()
                .mutableCopy {
                    setName {
                        displayName = contact.name
                    }
                    setOrganization {
                        company = contact.company
                        title = contact.title
                        department = contact.department
                    }
                    removeAllPhones()
                    contact.mobilePhone()?.let {
                        addPhone {
                            number = it
                            type = PhoneEntity.Type.MOBILE
                        }
                    }
                    contact.homePhone()?.let {
                        addPhone {
                            number = it
                            type = PhoneEntity.Type.HOME
                        }
                    }
                    contact.workPhone()?.let {
                        addPhone {
                            number = it
                            type = PhoneEntity.Type.WORK
                        }
                    }
                    contact.mainPhone()?.let {
                        addPhone {
                            number = it
                            type = PhoneEntity.Type.MAIN
                        }
                    }
                    contact.workFax()?.let {
                        addPhone {
                            number = it
                            type = PhoneEntity.Type.FAX_WORK
                        }
                    }
                    contact.homeFax()?.let {
                        addPhone {
                            number = it
                            type = PhoneEntity.Type.FAX_HOME
                        }
                    }
                    contact.pager()?.let {
                        addPhone {
                            number = it
                            type = PhoneEntity.Type.PAGER
                        }
                    }
                    contact.otherPhone()?.let {
                        addPhone {
                            number = it
                            type = PhoneEntity.Type.OTHER
                        }
                    }

                    removeAllEmails()
                    contact.homeEmail()?.let {
                        addEmail {
                            address = it
                            type = EmailEntity.Type.HOME
                        }
                    }
                    contact.workEmail()?.let {
                        addEmail {
                            address = it
                            type = EmailEntity.Type.WORK
                        }
                    }
                    contact.otherEmail()?.let {
                        addEmail {
                            address = it
                            type = EmailEntity.Type.OTHER
                        }
                    }

                    contact.avatarUri?.let {
                        getBytesFromImageUri(context, photoUri = Uri.parse(contact.avatarUri))?.let {
                            setPhoto(PhotoData.Companion.from(it))
                        }
                    }
                })
            .commit()
        return@withContext updateResult.isSuccessful.log { "editContact" }
    }

    override suspend fun addContact(contact: Contact): Boolean = withContext(Dispatchers.IO) {
        val addNewContactResult = Contacts(context)
            .insertWithPermission()
            .rawContact {
                setName {
                    displayName = contact.name
                }
                setOrganization {
                    company = contact.company
                    title = contact.title
                    department = contact.department
                }
//                removeAllPhones()
                contact.mobilePhone()?.let {
                    addPhone {
                        number = it
                        type = PhoneEntity.Type.MOBILE
                    }
                }
                contact.homePhone()?.let {
                    addPhone {
                        number = it
                        type = PhoneEntity.Type.HOME
                    }
                }
                contact.workPhone()?.let {
                    addPhone {
                        number = it
                        type = PhoneEntity.Type.WORK
                    }
                }
                contact.mainPhone()?.let {
                    addPhone {
                        number = it
                        type = PhoneEntity.Type.MAIN
                    }
                }
                contact.workFax()?.let {
                    addPhone {
                        number = it
                        type = PhoneEntity.Type.FAX_WORK
                    }
                }
                contact.homeFax()?.let {
                    addPhone {
                        number = it
                        type = PhoneEntity.Type.FAX_HOME
                    }
                }
                contact.pager()?.let {
                    addPhone {
                        number = it
                        type = PhoneEntity.Type.PAGER
                    }
                }
                contact.otherPhone()?.let {
                    addPhone {
                        number = it
                        type = PhoneEntity.Type.OTHER
                    }
                }

                removeAllEmails()
                contact.homeEmail()?.let {
                    addEmail {
                        address = it
                        type = EmailEntity.Type.HOME
                    }
                }
                contact.workEmail()?.let {
                    addEmail {
                        address = it
                        type = EmailEntity.Type.WORK
                    }
                }
                contact.otherEmail()?.let {
                    addEmail {
                        address = it
                        type = EmailEntity.Type.OTHER
                    }
                }

                contact.avatarUri?.let {
                    getBytesFromImageUri(context, photoUri = Uri.parse(it))?.let {
                        setPhoto(PhotoData.Companion.from(it))
                    }
                }
            }
            .commit()
        return@withContext addNewContactResult.isSuccessful
    }

    override suspend fun favoriteContact(contactId: Long, isFavorite: Boolean): Boolean = withContext(Dispatchers.IO) {
        val result = Contacts(context).query()
            .where {
                (Contact.Id equalTo contactId.toString())
            }
            .find()
            .firstOrNull()
            ?.mutableCopy {
                setOptions { starred = isFavorite }
            }?.let {
                Contacts(context)
                    .update()
                    .contacts(it)
                .commit()
            }
        return@withContext result!!.isSuccessful
    }

    override suspend fun deleteAllContacts(): Boolean {
        return false
    }

    fun doesContactExist(contactId: Long): Boolean {
        val contentResolver = context.contentResolver
        val uri = ContactsContract.Contacts.CONTENT_URI
        val projection = arrayOf(ContactsContract.Contacts._ID)
        val selection = "${ContactsContract.Contacts._ID} = ?"
        val selectionArgs = arrayOf(contactId.toString())

        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        val exists = cursor?.use { it.count > 0 }
        return exists ?: false
    }
}

fun getBytesFromImageUri(context: Context, photoUri: Uri?): ByteArray? {
    return try {
        val contentResolver = context.contentResolver
        val inputStream = photoUri?.let { contentResolver.openInputStream(it) }
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        // Resize the bitmap to reduce its size (e.g., 512x512)
        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 512, 512, true)

        val stream = ByteArrayOutputStream()
        // Compress the bitmap to JPEG format with a quality of 70%
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        stream.toByteArray()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}