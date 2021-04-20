package com.devlogs.chatty.domain.datasource.mainserver

import com.devlogs.chatty.domain.datasource.mainserver.model.StoryMainServerModel

interface StoryMainServerApi {
    suspend fun getPreviousStories(since:Long, count: Int) : List<StoryMainServerModel>
    suspend fun getStoriesOverPeriodOfTime (from: Long, to: Long) : List<StoryMainServerModel>
}