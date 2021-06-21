package com.devlogs.chatty.screen.chatscreen.chatscreen.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devlogs.chatty.screen.chatscreen.chatscreen.mvc_view.ChatMvcView
import com.devlogs.chatty.screen.chatscreen.chatscreen.mvc_view.getChatMvcView
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {
    companion object {
        fun getInstance () : ChatFragment {
            return ChatFragment()
        }
    }

    @Inject
    protected lateinit var mvcViewFactory: MvcViewFactory

    private lateinit var mvcView: ChatMvcView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mvcView = mvcViewFactory.getChatMvcView(container)
        return mvcView.getRootView()
    }
}