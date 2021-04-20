package com.devlogs.chatty.datasource.local.process

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.datasource.local.relam_object.MessageRealmObject
import com.devlogs.chatty.domain.entity.message.MessageEntity
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.withContext
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

    suspend fun getChannelMessageInPeriodOfTime (from: Long, to: Long) : List<MessageRealmObject> = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(realmConfiguration)

        val result = realmInstance
            .where(MessageRealmObject::class.java)
            .greaterThan("createdDate", from)
            .lessThan("createdDate", to)
            .findAll().toList()

        realmInstance.close()
        result
    }

    suspend fun getPreviousMessage (since: Long, count: Long = 10) : List<MessageRealmObject> = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(realmConfiguration)

        val result = realmInstance
            .where(MessageRealmObject::class.java)
            .lessThan("createdDate", since)
            .limit(count)
            .findAll().toList()

        realmInstance.close()
        result
    }

    suspend fun addNewMessages (messageROs: List<MessageRealmObject>) = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(realmConfiguration)
        realmInstance.executeTransaction {
            realmInstance.insert(messageROs)
        }

        realmInstance.close()
    }

}