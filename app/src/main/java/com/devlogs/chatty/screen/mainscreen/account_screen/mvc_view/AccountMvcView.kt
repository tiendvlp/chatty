package com.devlogs.chatty.screen.mainscreen.account_screen.mvc_view

import android.view.ViewGroup
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import com.devlogs.chatty.screen.common.mvcview.ObservableMvcView

interface AccountMvcView : ObservableMvcView<AccountMvcView.Listener> {
    interface Listener {
        fun onBtnSignOutClicked()
        fun onBtnInviteChannelClicked ()
    }
}

fun MvcViewFactory.getAccountMvcView  (container: ViewGroup?) :
        AccountMvcViewImp = AccountMvcViewImp(getLayoutInflater(), container)