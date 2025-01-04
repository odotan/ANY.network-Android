package com.anynetwork.app.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PhoneContactsDataSourceQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RoomContactsDataSourceQualifier