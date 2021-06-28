package com.devlogs.chatty.datasource.local.process

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.datasource.local.relam_object.MessageRealmObject
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class MessageLocalDbApi {

    private val realmConfiguration: RealmConfiguration

    @Inject
    constructor(realmConfiguration: RealmConfiguration) {
        this.realmConfiguration = realmConfiguration
    }

    suspend fun addNewMessage (messageRO: MessageRealmObject) = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(realmConfiguration)
        realmInstance.executeTransaction {
            realmInstance.insert(messageRO)
        }
        realmInstance.close()
    }

    suspend fun getChannelMessageInPeriodOfTime (channelId: String, from: Long, to: Long) : List<MessageRealmObject> = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(realmConfiguration)

        val result = realmInstance
            .where(MessageRealmObject::class.java)
            .equalTo("channelId", channelId)
            .greaterThan("createdDate", from)
            .lessThan("createdDate", to)
            .findAll().toList()

        realmInstance.close()
        result
    }

    suspend fun getPreviousMessage (channelId: String, since: Long, count: Int = 10) : List<MessageRealmObject> = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(realmConfiguration)

        val result = realmInstance
            .where(MessageRealmObject::class.java)
            .equalTo("channelId", channelId)
            .lessThan("createdDate", since)
            .limit(count.toLong())
            .findAll().toList()

        realmInstance.close()
        result
    }

    suspend fun getLatestUpdateTime (channelId: String) : Long {
        val result =  getPreviousMessage(channelId, Date().time, 1);
        normalLog("GetLatestUpdateTime Message: ${result}")
        if (result.isNotEmpty()) {
            return result[0].createdDate!!
        }
        return Date().time
    }

    suspend fun addNewMessages (messageROs: List<MessageRealmObject>) = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(realmConfiguration)
        realmInstance.executeTransaction {
            realmInstance.insert(messageROs)
        }
        realmInstance.close()
    }

}