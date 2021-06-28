package com.devlogs.chatty.datasource.local.process

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.datasource.local.relam_object.ProjectConfiguration
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class ConfigurationLocalDbApi {
    private val currentRealmConfiguration: RealmConfiguration

    @Inject
    constructor(realmConfiguration: RealmConfiguration) {
        currentRealmConfiguration = realmConfiguration
    }

    suspend fun getChannelLastUpdateTime () : Long = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        val result = realmInstance.where(ProjectConfiguration::class.java)
            .findFirst()
        if (result != null) {
            return@withContext result.lastUpdateChannelTime!!
        }
        Date().time
    }

    suspend fun setChannelLastUpdateTime (lastupdate: Long) = withContext(BackgroundDispatcher) {
        Realm.getInstance(currentRealmConfiguration).executeTransaction {
            val lastUpdateMessageTime = it.where(ProjectConfiguration::class.java).findFirst()?.lastUpdateMessageTime
            val newConfiguration = ProjectConfiguration(lastupdate, lastUpdateMessageTime)
                it.copyToRealmOrUpdate(newConfiguration, )
        }
    }

    suspend fun getMessageLastUpdateTime () : Long = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        val result = realmInstance.where(ProjectConfiguration::class.java)
            .findFirst()
        if (result != null) {
            return@withContext result.lastUpdateMessageTime!!
        }
        Date().time
    }

    suspend fun setMessageLastUpdateTime (lastupdate: Long) = withContext(BackgroundDispatcher) {
        Realm.getInstance(currentRealmConfiguration).executeTransaction {
            var lastUpdateChannelTime = it.where(ProjectConfiguration::class.java).findFirst()?.lastUpdateChannelTime
            val newConfiguration = ProjectConfiguration(lastUpdateChannelTime, lastupdate)
            it.copyToRealmOrUpdate(newConfiguration, )
        }
    }
}