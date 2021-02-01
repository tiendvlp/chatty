package com.devlogs.chatty.screen.authenticationscreen.registerscreen.mvc_view

import com.devlogs.chatty.screen.common.mvcview.ObservableMvcView

interface RegisterMvcView : ObservableMvcView<RegisterMvcView.Listener> {
    interface Listener {
        fun onBtnRegisterClicked (email: String, password: String)
        fun onBtnSignInClicked ()
    }

    fun loading ()
    fun registerFailed (errorMessage: String)
    fun registerSuccess ()

}