package com.devlogs.chatty.screen.authenticationscreen.loginscreen.mvc_view

import android.view.ViewGroup
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import com.devlogs.chatty.screen.common.mvcview.ObservableMvcView

interface LoginMvcView : ObservableMvcView<LoginMvcView.Listener> {

    interface Listener {
        fun onBtnLoginClicked(email: String, password: String)
        fun onBtnRegisterClicked()
        fun onBtnForgotPasswordClicked()
    }

    fun loading()
    fun loginFailed(errorMessage: String)
    fun loginSuccess()
}

fun MvcViewFactory.getLoginMvcView(container: ViewGroup?): LoginMvcView =
        LoginMvcViewImp(mLayoutInflater, container)
