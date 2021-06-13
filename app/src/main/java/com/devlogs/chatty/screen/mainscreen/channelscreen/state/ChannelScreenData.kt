package com.devlogs.chatty.screen.mainscreen.channelscreen.state

import android.graphics.Bitmap
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.ChannelPresentationModel
import java.util.*
import kotlin.collections.ArrayList

class ChannelScreenData {
    var userAvatar: Bitmap? = null
    var channels: TreeSet<ChannelPresentationModel> = TreeSet()
}