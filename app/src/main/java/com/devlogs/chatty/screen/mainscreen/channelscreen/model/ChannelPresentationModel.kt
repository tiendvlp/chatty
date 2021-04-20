package com.devlogs.chatty.screen.mainscreen.channelscreen.model

import android.graphics.Bitmap

data class ChannelPresentationModel(
    val avatar: List<Bitmap>, val title: String, val message:String, val sender: String
        )
