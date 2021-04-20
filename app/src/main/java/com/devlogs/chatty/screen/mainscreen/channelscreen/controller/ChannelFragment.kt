package com.devlogs.chatty.screen.mainscreen.channelscreen.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devlogs.chatty.channel.GetUserChannelsOverPeriodOfTimeUseCaseSync
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.login.LoginWithEmailUseCaseSync
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import com.devlogs.chatty.screen.mainscreen.channelscreen.mvc_view.ChannelMvcView
import com.devlogs.chatty.screen.mainscreen.channelscreen.mvc_view.getMainMvcView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ChannelFragment : Fragment() {

    companion object {
        fun getInstance () : ChannelFragment {
            return ChannelFragment()
        }
    }

    @Inject
    lateinit var loginWithEmailUseCaseSync: LoginWithEmailUseCaseSync
    @Inject
    lateinit var getChannelOverPeriodOfTimeUseCaseSync: GetUserChannelsOverPeriodOfTimeUseCaseSync
    @Inject
    lateinit var mvcViewFactory: MvcViewFactory
    private lateinit var mChannelMvcView : ChannelMvcView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            loginWithEmailUseCaseSync.execute("mingting17@mintin.com", "tiendvlp")
            val result = getChannelOverPeriodOfTimeUseCaseSync.execute(10, Date().time)
            if (result is GetUserChannelsOverPeriodOfTimeUseCaseSync.Result.Success) {
                result.channels.forEach {
                    normalLog("channelTitle: " + it.title)
                    normalLog("channelStatusContent: " + it.status.content)
                    normalLog("channelAdmin: " + it.admin)
                    normalLog("channelCreatedDate: " + it.createdDate)
                    normalLog("channelLatestUpdate: " + it.latestUpdate)
                    normalLog("channelId: " + it.id)
                    normalLog("channelAvatar: " + it.members[1].avatar.size)

                }
            } else {
                normalLog("Failure");
            }
        }
        mChannelMvcView = mvcViewFactory.getMainMvcView(container)
        return mChannelMvcView.getRootView()
    }
}