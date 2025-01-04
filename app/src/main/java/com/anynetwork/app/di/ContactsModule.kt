package com.anynetwork.app.di

import android.content.Context
import com.anynetwork.app.data.contacts.ContactsDataSource
import com.anynetwork.app.data.contacts.PhoneContactsDataSource
import com.anynetwork.app.data.contacts.RoomContactsDataSource
import com.anynetwork.app.db.DatabaseProvider
import com.anynetwork.app.utils.PermissionsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ContactsModule {

//    @Provides
//    fun provideContactsDao(@ApplicationContext context: Context): ContactsDao {
//        return Room.databaseBuilder(
//            context.applicationContext,
//            ContactsDatabase::class.java, "contacts_db"
//        ).build().contactsDao()
//    }

    @Provides
    @PhoneContactsDataSourceQualifier
    fun providePhoneContactsDataSource(@ApplicationContext context: Context): ContactsDataSource {
        return PhoneContactsDataSource(context)
    }

    @Provides
    @RoomContactsDataSourceQualifier
    fun provideRoomContactsDataSource(@ApplicationContext context: Context): ContactsDataSource {
        return RoomContactsDataSource(
            contactDao = DatabaseProvider.getDatabase(context).contactDao()
        )
    }

    @Provides
    fun providePermissionManager(@ApplicationContext context: Context): PermissionsManager {
        return PermissionsManager(context)
    }
}
