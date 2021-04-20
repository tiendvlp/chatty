package com.devlogs.chatty.datasource.local.process

import com.devlogs.chatty.common.CHANNEL_LIMIT
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.datasource.local.relam_object.ChannelRealmObject
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChannelLocalDbApi {
    private val currentRealmConfiguration: RealmConfiguration

    @Inject
    constructor(realmConfiguration: RealmConfiguration) {
        currentRealmConfiguration = realmConfiguration
    }

    suspend fun addChannel (channelRO: ChannelRealmObject) = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        realmInstance.executeTransaction {
            it.insert(channelRO)
        }
        realmInstance.close()
    }

    suspend fun removeChannel (id: String) : ChannelRealmObject? = withContext(BackgroundDispatcher){
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        val result = realmInstance
            .where(ChannelRealmObject::class.java)
            .equalTo("id", id)
            .findFirst()
        realmInstance.close()
        result
    }

    suspend fun getAllChannel () : List<ChannelRealmObject> = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        val result = realmInstance
            .where(ChannelRealmObject::class.java)
            .findAll().toList()
        realmInstance.close()
        result
    }

    suspend fun getPreviousChannels (since: Long, count: Int) : List<ChannelRealmObject> = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        val result = realmInstance
            .where(ChannelRealmObject::class.java)
            .lessThan("latestUpdate", since)
            .limit(CHANNEL_LIMIT)
            .findAll().toList()
        realmInstance.close()
        result
    }

    suspend fun getChannelsInPeriodOfTime (from: Long, to: Long) : List<ChannelRealmObject> = withContext(BackgroundDispatcher){
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        val result = realmInstance
            .where(ChannelRealmObject::class.java)
            .greaterThan("latestUpdate", from)
            .lessThan("latestUpdate", to)
            .findAll().toList()
        realmInstance.close()
        result
    }


}