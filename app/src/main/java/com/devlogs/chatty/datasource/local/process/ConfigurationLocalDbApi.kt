package com.devlogs.chatty.datasource.local.process

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.normalLog
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
            normalLog("Lendth: ${realmInstance.where(ProjectConfiguration::class.java).count()}")
            return@withContext result.lastUpdateChannelTime!!
        }
        normalLog("Lendth: ${realmInstance.where(ProjectConfiguration::class.java).count()}")
        Date().time
    }
    suspend fun setChannelLastUpdateTime (lastupdate: Long) = withContext(BackgroundDispatcher) {
        Realm.getInstance(currentRealmConfiguration).executeTransaction {
            val newConfiguration = ProjectConfiguration(lastupdate)
                it.copyToRealmOrUpdate(newConfiguration)
        }
    }
}