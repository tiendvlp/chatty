package com.devlogs.chatty.screen.mainscreen.channelscreen.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devlogs.chatty.channel.GetUserChannelsUseCaseSync
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
    lateinit var getChannelUseCaseSync: GetUserChannelsUseCaseSync
    @Inject
    lateinit var mvcViewFactory: MvcViewFactory
    private lateinit var mChannelMvcView : ChannelMvcView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        CoroutineScope(Dispatchers.Main.immediate).launch {
//            val result = getChannelUseCaseSync.execute(Date().time, 10)
//            if (result is GetUserChannelsUseCaseSync.Result.Success) {
//                result.channels.forEach {
//                    normalLog(it.title)
//                }
//            }
//        }
        mChannelMvcView = mvcViewFactory.getMainMvcView(container)
        return mChannelMvcView.getRootView()
    }
}