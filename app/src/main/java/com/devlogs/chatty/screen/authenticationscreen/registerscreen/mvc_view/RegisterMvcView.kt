package com.devlogs.chatty.screen.authenticationscreen.registerscreen.mvc_view

import com.devlogs.chatty.screen.common.mvcview.ObservableMvcView

interface RegisterMvcView : ObservableMvcView<RegisterMvcView.Listener> {
    interface Listener {
        fun onBtnRegisterClicked ()
        fun onBtnSignInClicked ()
    }

    fun showLoadingLayout ()
    fun hideLoadingLayout ()

}