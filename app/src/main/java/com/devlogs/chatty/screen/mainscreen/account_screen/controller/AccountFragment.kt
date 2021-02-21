package com.devlogs.chatty.screen.mainscreen.account_screen.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devlogs.chatty.R
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import com.devlogs.chatty.screen.mainscreen.account_screen.mvc_view.AccountMvcView
import com.devlogs.chatty.screen.mainscreen.account_screen.mvc_view.getAccountMvcView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : Fragment() {

    companion object {
        fun getInstance () : AccountFragment {
            return AccountFragment()
        }
    }

    @Inject
    lateinit var mvcViewFactory: MvcViewFactory
    private lateinit var mMvcView : AccountMvcView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mMvcView = mvcViewFactory.getAccountMvcView(container)
        return mMvcView.getRootView()
    }

}