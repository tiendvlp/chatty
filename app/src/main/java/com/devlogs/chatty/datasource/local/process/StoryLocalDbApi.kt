package com.devlogs.chatty.datasource.local.process

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.datasource.local.relam_object.StoryRealmObject
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StoryLocalDbApi {
    private val currentRealmConfiguration: RealmConfiguration

    @Inject
    constructor(realmConfiguration: RealmConfiguration) {
        currentRealmConfiguration = realmConfiguration
    }

    suspend fun addStory (storyRO: StoryRealmObject) = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        realmInstance.executeTransaction {
            it.insert(storyRO)
        }
        realmInstance.close()
    }

    suspend fun removeStory (id: String): Boolean = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        var result = false
        realmInstance.executeTransaction {
            it.where(StoryRealmObject::class.java).equalTo("id", id).findFirst()?.let { target ->
                target.deleteFromRealm()
                result = true
            }
        }

        result
    }

    suspend fun addStories (storyROs: List<StoryRealmObject>) = withContext(BackgroundDispatcher) {
        val realmInstance = Realm.getInstance(currentRealmConfiguration)
        realmInstance.executeTransaction {
            it.insert(storyROs)
        }
        realmInstance.close()
    }

}