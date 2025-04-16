package com.anynetwork.app.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anynetwork.app.model.Profile

@Entity(tableName = "profiles")
data class DbProfile(
    @PrimaryKey val id: Long = 1,
    val firstName: String = "",
    val lastName: String = "",
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
    val company: String = "",
    val address : String = "",
    val avatarUri: String? = null,
    val facebookProfileName: String? = null,
    val telegram: String? = null
)

fun DbProfile.unwrap(): Profile = Profile(
    firstName = this.firstName,
    lastName = this.lastName,
    mobilePhone = this.mobilePhone,
    homePhone = this.homePhone,
    workPhone = this.workPhone,
    mainPhone = this.mainPhone,
    workFax = this.workFax,
    homeFax = this.homeFax,
    otherPhone = this.otherPhone,
    homeEmail = this.homeEmail,
    workEmail = this.workEmail,
    otherEmail = this.otherEmail,
    company = this.company,
    address = this.address,
    avatarUri = this.avatarUri,
    facebookProfileName = this.facebookProfileName,
    telegram = this.telegram
)

fun Profile.wrap(): DbProfile = DbProfile(
    id = 0,
    firstName = this.firstName,
    lastName = this.lastName,
    mobilePhone = this.mobilePhone,
    homePhone = this.homePhone,
    workPhone = this.workPhone,
    mainPhone = this.mainPhone,
    workFax = this.workFax,
    homeFax = this.homeFax,
    otherPhone = this.otherPhone,
    homeEmail = this.homeEmail,
    workEmail = this.workEmail,
    otherEmail = this.otherEmail,
    company = this.company,
    address = this.address,
    avatarUri = this.avatarUri,
    facebookProfileName = this.facebookProfileName,
    telegram = this.telegram
)