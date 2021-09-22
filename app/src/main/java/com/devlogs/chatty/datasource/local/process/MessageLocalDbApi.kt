package com.devlogs.chatty.datasource.local.process

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.datasource.local.relam_object.MessageRealmObject
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.Sort
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class MessageLocalDbApi {

    private val realmConfiguration : RealmConfiguration

    @Inject
    constructor(realmConfiguration: RealmConfiguration) {
        this.realmConfiguration = realmConfiguration;
    }

    suspend fun addOrUpdate (messageRO: MessageRealmObject) = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(realmConfiguration)
        realmInstance.executeTransaction {
            realmInstance.copyToRealmOrUpdate(messageRO)
        }
    }

    fun delete (id: String) : MessageRealmObject? {
        val realmInstance = Realm.getInstance(realmConfiguration)
        var isSuccess = false;
        var instanceToDelete : MessageRealmObject? = null
        realmInstance.executeTransaction() {
             instanceToDelete = realmInstance.where(MessageRealmObject::class.java).equalTo("id", id).findFirst()
            if (instanceToDelete != null) {
                instanceToDelete!!.deleteFromRealm()
                isSuccess = true
            } else {
                isSuccess = false
            }

        }
        if (isSuccess) return instanceToDelete
        return null
    }

    suspend fun getChannelMessageInPeriodOfTime (channelId: String, from: Long, to: Long) : List<MessageRealmObject> = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(realmConfiguration)
        val result = realmInstance
            .where(MessageRealmObject::class.java)
            .equalTo("channelId", channelId)
            .sort("createdDate", Sort.DESCENDING)
            .greaterThan("createdDate", from)
            .lessThan("createdDate", to)
            .findAll().toList()

        result
    }

    suspend fun getPreviousMessage (channelId: String, since: Long, count: Int) : List<MessageRealmObject> = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(realmConfiguration)
        val result = realmInstance
            .where(MessageRealmObject::class.java)
            .equalTo("channelId", channelId)
            .lessThan("createdDate", since)
            .sort("createdDate", Sort.DESCENDING)
            .limit(count.toLong())
            .findAll().toList()

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
            realmInstance.copyToRealmOrUpdate(messageROs)
        }
    }

    fun getMessage(id: String): MessageRealmObject? {
        val realmInstance = Realm.getInstance(realmConfiguration)
        return realmInstance.where(MessageRealmObject::class.java).equalTo("id", id).findFirst()
    }
}