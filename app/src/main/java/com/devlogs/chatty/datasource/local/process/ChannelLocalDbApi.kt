package com.devlogs.chatty.datasource.local.process

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.datasource.local.relam_object.ChannelRealmObject
import com.devlogs.chatty.domain.error.CommonErrorEntity
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class ChannelLocalDbApi {
    private val currentRealmConfiguration: RealmConfiguration

    @Inject
    constructor(realmConfiguration: RealmConfiguration) {
        currentRealmConfiguration = realmConfiguration

    }

    suspend fun addChannel (channelRO: ChannelRealmObject) = withContext(BackgroundDispatcher) {
        overrideChannel(listOf(channelRO))
    }

    suspend fun addChannel (channelROs: List<ChannelRealmObject>) = withContext(BackgroundDispatcher) {
        overrideChannel(channelROs)
    }

    suspend fun overrideChannel (channelROs: List<ChannelRealmObject>) = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        realmInstance.executeTransaction {
            it.copyToRealmOrUpdate(channelROs)
        }
    }
    suspend fun getAllChannel () : List<ChannelRealmObject> = withContext(BackgroundDispatcher) {

        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        val count = realmInstance.where(ChannelRealmObject::class.java)
            .count()
        val result = realmInstance
            .where(ChannelRealmObject::class.java)
            .sort("latestUpdate", Sort.DESCENDING)
            .findAll()
        result
    }

    fun getChannel (id: String) : ChannelRealmObject {
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        return realmInstance
            .where(ChannelRealmObject::class.java)
            .equalTo("id", id)
            .findFirst()
            ?: throw CommonErrorEntity.NotFoundErrorEntity("Channel (id=$id) does not exist in realm db")
    }

    suspend fun getPreviousChannels (since: Long, count: Int) : List<ChannelRealmObject> = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        val result = realmInstance
            .where(ChannelRealmObject::class.java)
            .sort("latestUpdate", Sort.DESCENDING)
            .lessThan("latestUpdate", since)
            .limit(count.toLong())
            .findAll()
        result
    }

    suspend fun getLatestUpdateTime () : Long {
        val result =  getPreviousChannels(Date().time, 1);
        normalLog("GetLatestUpdateTime: ${result}")
        if (result.isNotEmpty()) {
            return result[0].latestUpdate!!
        }
        return Date().time
    }

     fun getLatestLastUpdateTime () : Long {
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        val result = realmInstance.where(ChannelRealmObject::class.java)
            .sort("latestUpdate", Sort.ASCENDING)
            .findFirst()
        if (result != null) {
            return result!!.latestUpdate!!
        }
        return Date().time
    }

    suspend fun getChannelsInPeriodOfTime (from: Long, to: Long) : List<ChannelRealmObject> = withContext(BackgroundDispatcher){
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        val result = realmInstance
            .where(ChannelRealmObject::class.java)
            .greaterThan("latestUpdate", from)
            .lessThan("latestUpdate", to)
            .findAll()
        realmInstance.close()
        result
    }


}