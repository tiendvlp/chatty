package com.devlogs.chatty.screen.authenticationscreen.loginscreen.mvc_view

import com.devlogs.chatty.screen.common.mvcview.ObservableMvcView

interface LoginMvcView : ObservableMvcView <LoginMvcView.Listener> {

    interface Listener {
        fun onBtnLoginClicked(email: String, password: String)
        fun onBtnRegisterClicked ()
        fun onBtnForgotPasswordClicked ()
    }

}