package com.anynetwork.app.data.profile

import com.anynetwork.app.db.dao.ProfileDao
import com.anynetwork.app.db.entity.DbProfile
import com.anynetwork.app.db.entity.wrap
import com.anynetwork.app.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val profileDao: ProfileDao) {

    suspend fun getOrCreateDefaultProfile(): DbProfile = withContext(Dispatchers.IO) {
        val profile = profileDao.getProfile()
        profile ?: DbProfile(firstName = "", lastName = "").also {
            profileDao.insertProfile(it)
        }
    }

    suspend fun editProfile(profile: Profile) = withContext(Dispatchers.IO) {
        profileDao.insertProfile(profile.wrap())
    }

}