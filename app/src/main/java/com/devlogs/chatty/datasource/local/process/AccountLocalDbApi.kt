package com.devlogs.chatty.datasource.local.process

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.datasource.local.relam_object.AccountRealmObject
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountLocalDbApi {
    private val currentRealmConfiguration: RealmConfiguration

    @Inject
    constructor(realmConfiguration: RealmConfiguration) {
        currentRealmConfiguration = realmConfiguration
    }

    suspend fun setAccount (account: AccountRealmObject) = withContext(BackgroundDispatcher){
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        realmInstance.executeTransaction {
            realmInstance.delete(AccountRealmObject::class.java)
            it.insert(account)
        }
    }

    suspend fun getAccount () : AccountRealmObject? = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        val result = realmInstance.where(AccountRealmObject::class.java)
            .findFirst()
        result
    }


}