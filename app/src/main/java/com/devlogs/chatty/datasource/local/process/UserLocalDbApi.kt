package com.devlogs.chatty.datasource.local.process

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.datasource.local.relam_object.UserRealmObject
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserLocalDbApi {
    private val currentRealmConfiguration: RealmConfiguration

    @Inject
    constructor(realmConfiguration: RealmConfiguration) {
        currentRealmConfiguration = realmConfiguration
    }

    suspend fun addUser (userRO: UserRealmObject) = withContext(BackgroundDispatcher){
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        realmInstance.executeTransaction {
            it.insert(userRO)
        }
    }

    suspend fun getUser (userEmail: String) : UserRealmObject? = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        val result = realmInstance.where(UserRealmObject::class.java)
            .equalTo("email", userEmail)
            .findFirst()
        result
    }


}