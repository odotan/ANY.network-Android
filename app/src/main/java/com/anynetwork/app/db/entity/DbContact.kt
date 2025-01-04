package com.anynetwork.app.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anynetwork.app.model.Contact

@Entity(tableName = "contacts")
data class DbContact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val mobilePhone: String? = null,
    val homePhone: String? = null,
    val workPhone: String? = null,
    val mainPhone: String? = null,
    val workFax: String? = null,
    val homeFax: String? = null,
    val pager: String? = null,
    val otherPhone: String? = null,
    val homeEmail: String? = null,
    val workEmail: String? = null,
    val otherEmail: String? = null,
    val avatarUri: String? = null,
    val isFavorite: Boolean = false
)

fun DbContact.unwrap(): Contact = Contact(
    id = this.id,
    name = this.name,
    phones = mutableListOf<Contact.Phone>().apply {
        this@unwrap.mobilePhone?.let { add(Contact.Phone(Contact.Phone.Type.Mobile, it)) }
        this@unwrap.homePhone?.let { add(Contact.Phone(Contact.Phone.Type.Home, it)) }
        this@unwrap.workPhone?.let { add(Contact.Phone(Contact.Phone.Type.Work, it)) }
        this@unwrap.mainPhone?.let { add(Contact.Phone(Contact.Phone.Type.Main, it)) }
        this@unwrap.workFax?.let { add(Contact.Phone(Contact.Phone.Type.WorkFax, it)) }
        this@unwrap.homeFax?.let { add(Contact.Phone(Contact.Phone.Type.HomeFax, it)) }
        this@unwrap.pager?.let { add(Contact.Phone(Contact.Phone.Type.Pager, it)) }
        this@unwrap.otherPhone?.let { add(Contact.Phone(Contact.Phone.Type.Other, it)) }
    },
    emails = mutableListOf<Contact.Email>().apply {
        this@unwrap.homeEmail?.let { add(Contact.Email(Contact.Email.Type.Home, it)) }
        this@unwrap.workEmail?.let { add(Contact.Email(Contact.Email.Type.Work, it)) }
        this@unwrap.otherEmail?.let { add(Contact.Email(Contact.Email.Type.Other, it)) }
    },
    avatarUri = this.avatarUri,
    isFavorite = this.isFavorite,
)

fun Contact.wrap(): DbContact = DbContact(
    id = this.id,
    name = this.name,
    mobilePhone = this.mobilePhone(),
    homePhone = this.homePhone(),
    workPhone = this.workPhone(),
    mainPhone = this.mainPhone(),
    workFax = this.workFax(),
    homeFax = this.homeFax(),
    pager = this.pager(),
    otherPhone = this.otherPhone(),
    homeEmail = this.homeEmail(),
    workEmail = this.workPhone(),
    otherEmail = this.otherEmail(),
    avatarUri = this.avatarUri,
    isFavorite = this.isFavorite
)