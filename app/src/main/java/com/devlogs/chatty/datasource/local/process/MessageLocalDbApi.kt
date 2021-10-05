package com.devlogs.chatty.datasource.local.process

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.datasource.local.relam_object.MessageRealmObject
import com.devlogs.chatty.domain.entity.message.MessageEntity
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

     fun addOrUpdate (messageRO: MessageRealmObject)  {
        val realmInstance = Realm.getInstance(realmConfiguration)
        realmInstance.executeTransaction {
            it.copyToRealmOrUpdate(messageRO)
        }
        realmInstance.close();
    }

    fun delete (id: String) : MessageRealmObject? {
        val realmInstance = Realm.getInstance(realmConfiguration)
        var isSuccess = false;
        var instanceToDelete : MessageRealmObject? = null
        realmInstance.executeTransaction() {
             instanceToDelete = it.where(MessageRealmObject::class.java).equalTo("id", id).findFirst()
            if (instanceToDelete != null) {
                instanceToDelete!!.deleteFromRealm()
                isSuccess = true
            } else {
                isSuccess = false
            }

        }
        realmInstance.close()
        if (isSuccess) return instanceToDelete
        return null

    }

     fun getChannelMessageInPeriodOfTime (channelId: String, from: Long, to: Long) : List<MessageRealmObject>  {
        val realmInstance = Realm.getInstance(realmConfiguration)
        val result = realmInstance
            .where(MessageRealmObject::class.java)
            .equalTo("channelId", channelId)
            .sort("createdDate", Sort.DESCENDING)
            .greaterThan("createdDate", from)
            .lessThan("createdDate", to)
            .findAll().toList()
        realmInstance.close();
        return result
    }

     fun getPreviousMessage (channelId: String, since: Long, count: Int) : List<MessageRealmObject> {
        val realmInstance = Realm.getInstance(realmConfiguration)
        val result = realmInstance
            .where(MessageRealmObject::class.java)
            .equalTo("channelId", channelId)
            .lessThan("createdDate", since)
            .sort("createdDate", Sort.DESCENDING)
            .limit(count.toLong())
            .findAll().toList()
        realmInstance.close();

        return result
    }

     fun getLatestUpdateTime (channelId: String) : Long {
        val realmInstance = Realm.getInstance(realmConfiguration)
        val result =  getPreviousMessage(channelId, Date().time, 1);
        normalLog("GetLatestUpdateTime Message: ${result}")
        if (result.isNotEmpty()) {
            return result[0].createdDate!!
        }
        return Date().time
    }

     fun addNewMessages (messageROs: List<MessageRealmObject>) {
        val realmInstance = Realm.getInstance(realmConfiguration)
        realmInstance.executeTransaction {
            it.copyToRealmOrUpdate(messageROs)
        }
         realmInstance.close()
    }

     fun getAll () : List<MessageRealmObject> {
        val realmInstance = Realm.getInstance(realmConfiguration)
        val result = realmInstance.where(MessageRealmObject::class.java).findAll().toList()

        return result
    }

     fun getMessageByState (state: MessageEntity.Status) : List<MessageRealmObject>{
        val realmInstance = Realm.getInstance(realmConfiguration)
        val result = realmInstance.where(MessageRealmObject::class.java).equalTo("status", state.name).findAll().toList()
        return result
    }

     fun getMessage(id: String): MessageRealmObject?  {
        val realmInstance = Realm.getInstance(realmConfiguration)
        var result = realmInstance.where(MessageRealmObject::class.java).equalTo("id", id).findFirst()
        return result
    }
}