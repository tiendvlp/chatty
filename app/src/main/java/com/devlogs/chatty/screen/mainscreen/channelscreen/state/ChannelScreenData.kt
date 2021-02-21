package com.devlogs.chatty.screen.mainscreen.channelscreen.state

import android.graphics.Bitmap
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.ChannelPresentationModel

class ChannelScreenData {
    var userAvatar: Bitmap? = null
    var channels: List<ChannelPresentationModel> = ArrayList()
}