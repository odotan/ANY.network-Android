package com.anynetwork.app.model

class Profile(
    val firstName: String,
    val lastName: String,
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
)