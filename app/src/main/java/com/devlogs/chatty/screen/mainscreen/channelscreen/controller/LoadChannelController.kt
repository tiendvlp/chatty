package com.devlogs.chatty.screen.mainscreen.channelscreen.controller

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.devlogs.chatty.channel.GetUserChannelsUseCaseSync
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.ChannelPresentationModel
import com.devlogs.chatty.screen.mainscreen.channelscreen.state.ChannelScreenPresentationAction
import com.devlogs.chatty.screen.mainscreen.channelscreen.state.ChannelScreenPresentationAction.LoadChannelSuccessAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class LoadChannelController {
    private val mPresentationStateManager: PresentationStateManager
    private val mGetChannelUseCaseSync: GetUserChannelsUseCaseSync
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    constructor(
        getChannelUseCaseSync: GetUserChannelsUseCaseSync,
        presentationStateManager: PresentationStateManager
    ) {
        mGetChannelUseCaseSync = getChannelUseCaseSync
        mPresentationStateManager = presentationStateManager
    }

    fun getChannels(since: Long) {
        scope.launch {
            val result = mGetChannelUseCaseSync.execute(since, 10)

            if (result is GetUserChannelsUseCaseSync.Result.Success) {
                mPresentationStateManager.consumeAction(LoadChannelSuccessAction(result.channels.map {
                    ChannelPresentationModel(
                        loadAvatar(), it.title,
                        it.status.description.content.toString(),
                        it.status.senderEmail
                    )
                }))
            }
            if (result is GetUserChannelsUseCaseSync.Result.NetworkError) {
                mPresentationStateManager.consumeAction(
                    ChannelScreenPresentationAction.LoadChannelFailedAction(
                        "Network Error"
                    )
                )
            }
            if (result is GetUserChannelsUseCaseSync.Result.GeneralError) {
                mPresentationStateManager.consumeAction(
                    ChannelScreenPresentationAction.LoadChannelFailedAction(
                        "General Error"
                    )
                )
            }
            if (result is GetUserChannelsUseCaseSync.Result.InvalidRefreshToken) {
                mPresentationStateManager.consumeAction(
                    ChannelScreenPresentationAction.LoadChannelFailedAction(
                        "UnAuthorized"
                    )
                )
            }
        }
    }


    private suspend fun loadAvatar () : Bitmap = withContext(BackgroundDispatcher) {
        val url = URL("http://image10.bizrate-images.com/resize?sq=60&uid=2216744464")
        BitmapFactory.decodeStream(url.openConnection().getInputStream())
    }
}

open class AA() {
    fun main() {
        val students = arrayOf("Abel", "Bill", "Cindy", "Darla")
        var a = ""
        Pair(0, 1)
        printStudents(*students)
    }
    fun printStudents(vararg students: String) {
        for (student in students) println(student)
    }
}
class BB() : AA() {
    var price: Int = 0
        get() = field + 20
}